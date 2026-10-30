# Azure setup (one time)

The deploy workflow needs four things that Terraform cannot create for itself: a place to keep its state, an identity GitHub can sign in as, permission for that identity, and six GitHub variables. Run these once with the Azure CLI as someone who can create role assignments on the subscription.

> **Region:** `infra/terraform/envs/devqa.tfvars` is set to `canadacentral`, as in the Procurement Handover v1.2 and Build Plan v1.1. Change it to `canadaeast` before the first deploy if the Dev+QA environment is going to Quebec. Changing it later means rebuilding the environment.

## 1. Terraform state storage

```bash
SUB=<subscription-id>
LOC=canadacentral
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

## 3. Permissions

The deploy identity creates resources **and** role assignments (AcrPull, Key Vault roles), so it needs both:

```bash
az role assignment create --assignee $APP_ID --role Contributor --scope /subscriptions/$SUB
az role assignment create --assignee $APP_ID --role "Role Based Access Control Administrator" \
  --scope /subscriptions/$SUB
az role assignment create --assignee $APP_ID --role "Storage Blob Data Contributor" \
  --scope $(az storage account show -n stmyclawdtfstate<unique> --query id -o tsv)
```

Scope these to a dedicated resource group instead of the subscription if your policy requires it (create `rg-myclawd-devqa` first and add `import` for it in Terraform).

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
