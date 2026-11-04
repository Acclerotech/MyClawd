# Azure setup (one time)

The deploy workflow needs four things that Terraform cannot create for itself: a place to keep its state, an identity GitHub can sign in as, permission for that identity, and six GitHub variables.

> **Region:** Canada East (Quebec), set in `infra/terraform/envs/devqa.tfvars`. Changing it after the first deploy means rebuilding the environment.

## Quick way: one script

Open **Azure Cloud Shell** (portal.azure.com → the `>_` icon → **Bash**) signed in as a **subscription Owner**, then:

```bash
curl -fsSL https://raw.githubusercontent.com/Acclerotech/MyClawd/main/scripts/azure-bootstrap.sh -o bootstrap.sh
bash bootstrap.sh <subscription-id>
```

It does steps 1–3 below (plus registering resource providers and checking the region), is safe to re-run, and prints the exact values for step 4. The manual steps are kept for reference.

## 1. Terraform state storage

```bash
SUB=<subscription-id>
LOC=canadaeast
az account set --subscription $SUB

az group create -n rg-myclawd-tfstate -l $LOC
az storage account create -n stmyclawdtfstate<unique> -g rg-myclawd-tfstate -l $LOC \
  --sku Standard_LRS --min-tls-version TLS1_2 --allow-blob-public-access false
az storage container create -n tfstate --account-name stmyclawdtfstate<unique> --auth-mode login
```

## 2. Identity for GitHub Actions (OIDC — no secrets stored in GitHub)

```bash
APP_ID=$(az ad app create --display-name "github-myclawd-deploy" --query appId -o tsv)
az ad sp create --id $APP_ID

# Trust the "devqa" GitHub environment of the repository
az ad app federated-credential create --id $APP_ID --parameters '{
  "name": "github-devqa",
  "issuer": "https://token.actions.githubusercontent.com",
  "subject": "repo:<owner>/<repo>:environment:devqa",
  "audiences": ["api://AzureADTokenExchange"]
}'
```

## 3. Resource group, app identity and permissions

The bootstrap script creates `rg-myclawd-devqa` and the apps' managed identity `id-myclawd-devqa-apps`, then grants:

| Who | Role | Scope |
| --- | --- | --- |
| deploy identity | Contributor | subscription |
| deploy identity | Storage Blob Data Contributor | Terraform state storage account |
| deploy identity | Key Vault Secrets Officer | `rg-myclawd-devqa` |
| apps identity | AcrPull | `rg-myclawd-devqa` |
| apps identity | Key Vault Secrets User | `rg-myclawd-devqa` |

Terraform does not create role assignments, so the deploy identity never needs Owner, User Access Administrator or Role Based Access Control Administrator. This also works when the subscription Owner's own role carries Azure's condition that blocks granting privileged roles.

## 4. GitHub

In the repository: **Settings → Environments → New environment → `devqa`** (add required reviewers if deploys should wait for approval).

**Settings → Secrets and variables → Actions → Variables**:

| Variable | Value |
| --- | --- |
| `AZURE_CLIENT_ID` | `$APP_ID` |
| `AZURE_TENANT_ID` | `az account show --query tenantId -o tsv` |
| `AZURE_SUBSCRIPTION_ID` | `$SUB` |
| `TF_STATE_RESOURCE_GROUP` | `rg-myclawd-tfstate` |
| `TF_STATE_STORAGE_ACCOUNT` | `stmyclawdtfstate<unique>` |
| `TF_STATE_CONTAINER` | `tfstate` |

Then run **Actions → Deploy to Azure (Dev+QA) → Run workflow**. The first run creates the registry and database, pushes the images, then creates the 13 container apps. The run summary shows the web and gateway URLs.

## Cost notes

- Every app scales to zero (`app_min_replicas = 0`). The first request after idle takes ~15–30 s while a Spring Boot service starts. For QA windows set `app_min_replicas = 1` and redeploy, then set it back.
- MySQL is Burstable B1ms; stop it outside working hours with `az mysql flexible-server stop` to save compute.
- The resource-group budget alert covers Azure-billed spend only. Set `budget_alert_emails` in `devqa.tfvars` to switch it on.
