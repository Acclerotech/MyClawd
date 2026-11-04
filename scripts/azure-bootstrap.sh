#!/usr/bin/env bash
# One-time Azure setup for MyClawd Dev+QA. Run it in Azure Cloud Shell (Bash) as a subscription Owner:
#
#   curl -fsSL https://raw.githubusercontent.com/Acclerotech/MyClawd/main/scripts/azure-bootstrap.sh -o bootstrap.sh
#   bash bootstrap.sh <subscription-id>
#
# It is safe to run again: everything it creates is looked up first and reused.
# What it does (docs/azure-setup.md explains each step):
#   1. registers the Azure resource providers MyClawd needs
#   2. checks the region offers Container Apps and MySQL Flexible Server
#   3. creates the Terraform state storage
#   4. creates the identity GitHub Actions signs in as (OIDC - no secrets stored in GitHub)
#   5. gives that identity the permissions Terraform needs
#   6. prints the six GitHub variables to add
set -euo pipefail

SUB="${1:?Usage: bash azure-bootstrap.sh <subscription-id> [location] [owner/repo]}"
LOCATION="${2:-canadaeast}"
REPO="${3:-Acclerotech/MyClawd}"
GH_ENV="devqa"
STATE_RG="rg-myclawd-tfstate"
STATE_CONTAINER="tfstate"
APP_NAME="github-myclawd-deploy"

say() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }

say "Using subscription $SUB, region $LOCATION, repository $REPO"
az account set --subscription "$SUB"
# Accept a subscription name too, but always work with (and print) its GUID.
SUB=$(az account show --query id -o tsv)
TENANT=$(az account show --query tenantId -o tsv)
az account show --query "{name:name, state:state}" -o table

say "1/6 Registering resource providers (first time can take a few minutes)"
for ns in Microsoft.App Microsoft.OperationalInsights Microsoft.ContainerRegistry Microsoft.DBforMySQL \
          Microsoft.KeyVault Microsoft.ManagedIdentity Microsoft.Storage Microsoft.Consumption; do
  state=$(az provider show -n "$ns" --query registrationState -o tsv 2>/dev/null || echo NotRegistered)
  if [[ "$state" != "Registered" ]]; then
    echo "  registering $ns"
    az provider register -n "$ns" --wait >/dev/null
  else
    echo "  $ns already registered"
  fi
done

say "2/6 Checking $LOCATION supports Container Apps and MySQL Flexible Server"
loc_display=$(az account list-locations --query "[?name=='$LOCATION'].displayName | [0]" -o tsv)
[[ -n "$loc_display" ]] || { echo "Unknown region: $LOCATION"; exit 1; }
for check in "Microsoft.App managedEnvironments" "Microsoft.DBforMySQL flexibleServers"; do
  set -- $check
  if az provider show -n "$1" --query "resourceTypes[?resourceType=='$2'].locations[]" -o tsv | grep -qix "$loc_display"; then
    echo "  $1/$2 available in $loc_display"
  else
    echo "  $1/$2 is NOT available in $loc_display - stop and choose another region"; exit 1
  fi
done
if az mysql flexible-server list-skus --location "$LOCATION" -o json 2>/dev/null | grep -q '"Standard_B1ms"'; then
  echo "  MySQL Burstable B1ms offered in $loc_display"
else
  echo "  WARNING: could not confirm MySQL B1ms in $loc_display for this subscription (check quota/offer restrictions)"
fi

say "3/6 Terraform state storage"
az group create -n "$STATE_RG" -l "$LOCATION" -o none
# Storage account names are global. Reuse the one from an earlier run if it exists,
# otherwise derive a stable name from the subscription id.
SA=$(az storage account list -g "$STATE_RG" --query "[?starts_with(name,'stmyclawdtf')].name | [0]" -o tsv)
[[ -n "$SA" ]] || SA="stmyclawdtf$(echo -n "$SUB" | sha1sum | cut -c1-10)"
if ! az storage account show -n "$SA" -g "$STATE_RG" -o none 2>/dev/null; then
  az storage account create -n "$SA" -g "$STATE_RG" -l "$LOCATION" --sku Standard_LRS \
    --kind StorageV2 --min-tls-version TLS1_2 --allow-blob-public-access false \
    --allow-shared-key-access false -o none
fi
SA_ID=$(az storage account show -n "$SA" -g "$STATE_RG" --query id -o tsv)
az storage account blob-service-properties update --account-name "$SA" -g "$STATE_RG" \
  --enable-versioning true --enable-delete-retention true --delete-retention-days 14 -o none
# You need data access yourself to create the container with Entra auth.
ME=$(az ad signed-in-user show --query id -o tsv)
az role assignment create --assignee-object-id "$ME" --assignee-principal-type User \
  --role "Storage Blob Data Contributor" --scope "$SA_ID" -o none 2>/dev/null || true
for i in 1 2 3 4 5 6; do
  az storage container create -n "$STATE_CONTAINER" --account-name "$SA" --auth-mode login -o none 2>/dev/null && break
  echo "  waiting for storage permission to take effect..."; sleep 20
done
echo "  state: $SA / $STATE_CONTAINER"

say "4/6 Identity for GitHub Actions"
APP_ID=$(az ad app list --display-name "$APP_NAME" --query "[0].appId" -o tsv)
if [[ -z "$APP_ID" ]]; then
  APP_ID=$(az ad app create --display-name "$APP_NAME" --query appId -o tsv)
fi
az ad sp show --id "$APP_ID" -o none 2>/dev/null || az ad sp create --id "$APP_ID" -o none
SP_ID=$(az ad sp show --id "$APP_ID" --query id -o tsv)
SUBJECT="repo:${REPO}:environment:${GH_ENV}"
if ! az ad app federated-credential list --id "$APP_ID" --query "[?subject=='$SUBJECT']" -o tsv | grep -q .; then
  az ad app federated-credential create --id "$APP_ID" --parameters "{
    \"name\": \"github-${GH_ENV}\",
    \"issuer\": \"https://token.actions.githubusercontent.com\",
    \"subject\": \"${SUBJECT}\",
    \"audiences\": [\"api://AzureADTokenExchange\"]
  }" -o none
fi
echo "  $APP_NAME ($APP_ID) trusts $SUBJECT"

say "5/6 Permissions for the deploy identity"
assign() {
  if [[ -n "$(az role assignment list --assignee "$SP_ID" --role "$1" --scope "$2" --query "[0].id" -o tsv)" ]]; then
    echo "  $1 (already assigned)"
  else
    az role assignment create --assignee-object-id "$SP_ID" --assignee-principal-type ServicePrincipal \
      --role "$1" --scope "$2" -o none
    echo "  $1 (assigned)"
  fi
}
assign "Contributor" "/subscriptions/$SUB"
assign "Role Based Access Control Administrator" "/subscriptions/$SUB"
assign "Storage Blob Data Contributor" "$SA_ID"

say "Checking the deploy identity's permissions"
for role in "Contributor" "Role Based Access Control Administrator"; do
  if [[ -z "$(az role assignment list --assignee "$SP_ID" --role "$role" --scope "/subscriptions/$SUB" --query "[0].id" -o tsv)" ]]; then
    echo "  MISSING: $role on the subscription - are you signed in as a subscription Owner?"; exit 1
  fi
  echo "  ok: $role"
done

say "6/6 Add these to GitHub"
cat <<EOF

  1) https://github.com/${REPO}/settings/environments  ->  New environment  ->  ${GH_ENV}

  2) https://github.com/${REPO}/settings/variables/actions  ->  New repository variable (x6):

     AZURE_CLIENT_ID           ${APP_ID}
     AZURE_TENANT_ID           ${TENANT}
     AZURE_SUBSCRIPTION_ID     ${SUB}
     TF_STATE_RESOURCE_GROUP   ${STATE_RG}
     TF_STATE_STORAGE_ACCOUNT  ${SA}
     TF_STATE_CONTAINER        ${STATE_CONTAINER}

  These are identifiers, not secrets.

  3) https://github.com/${REPO}/actions/workflows/deploy.yml  ->  Run workflow

EOF
