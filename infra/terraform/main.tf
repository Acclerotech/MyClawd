data "azurerm_client_config" "current" {}

resource "random_string" "suffix" {
  length  = 4
  upper   = false
  special = false
}

locals {
  base   = "${var.name_prefix}-${var.environment}"
  suffix = random_string.suffix.result
}

# ---------------------------------------------------------------------------
# Resource group, logging, registry, identity
# ---------------------------------------------------------------------------

resource "azurerm_resource_group" "rg" {
  name     = "rg-${local.base}"
  location = var.location
  tags     = var.tags
}

resource "azurerm_log_analytics_workspace" "logs" {
  name                = "log-${local.base}"
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
  daily_quota_gb      = 1 # capped ingestion (Tech Stack: observability)
  tags                = var.tags
}

resource "azurerm_container_registry" "acr" {
  name                = "acr${var.name_prefix}${var.environment}${local.suffix}"
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "Basic"
  admin_enabled       = false
  tags                = var.tags
}

# One identity the apps use to pull images and read Key Vault secrets.
resource "azurerm_user_assigned_identity" "apps" {
  name                = "id-${local.base}-apps"
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name
  tags                = var.tags
}

resource "azurerm_role_assignment" "apps_acr_pull" {
  scope                = azurerm_container_registry.acr.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.apps.principal_id
}

# ---------------------------------------------------------------------------
# Key Vault (secrets only in Key Vault - Dev Principle 10)
# ---------------------------------------------------------------------------

resource "azurerm_key_vault" "kv" {
  name                       = "kv-${var.name_prefix}-${var.environment}-${local.suffix}"
  location                   = var.location
  resource_group_name        = azurerm_resource_group.rg.name
  tenant_id                  = data.azurerm_client_config.current.tenant_id
  sku_name                   = "premium"
  rbac_authorization_enabled = true
  purge_protection_enabled   = false
  soft_delete_retention_days = 7
  tags                       = var.tags
}

resource "azurerm_role_assignment" "deployer_kv_secrets" {
  scope                = azurerm_key_vault.kv.id
  role_definition_name = "Key Vault Secrets Officer"
  principal_id         = data.azurerm_client_config.current.object_id
}

resource "azurerm_role_assignment" "apps_kv_secrets" {
  scope                = azurerm_key_vault.kv.id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_user_assigned_identity.apps.principal_id
}

resource "random_password" "mysql" {
  length           = 32
  special          = true
  override_special = "_-.~"
}

resource "azurerm_key_vault_secret" "mysql_password" {
  name         = "mysql-admin-password"
  value        = random_password.mysql.result
  key_vault_id = azurerm_key_vault.kv.id
  depends_on   = [azurerm_role_assignment.deployer_kv_secrets]
}

# ---------------------------------------------------------------------------
# MySQL Flexible Server 8.4 LTS - one server, one schema per service
# azapi is used because the azurerm resource does not yet accept version "8.4".
# ---------------------------------------------------------------------------

resource "azapi_resource" "mysql" {
  type                      = "Microsoft.DBforMySQL/flexibleServers@2023-12-30"
  name                      = "mysql-${local.base}-${local.suffix}"
  parent_id                 = azurerm_resource_group.rg.id
  location                  = var.location
  schema_validation_enabled = false
  tags                      = var.tags

  body = {
    sku = {
      name = var.mysql_sku
      tier = "Burstable"
    }
    properties = {
      version            = "8.4"
      administratorLogin = var.mysql_admin_login
      storage = {
        storageSizeGB = 20
        autoGrow      = "Enabled"
      }
      backup = {
        backupRetentionDays = 7
        geoRedundantBackup  = "Disabled"
      }
      highAvailability = { mode = "Disabled" }
      network          = { publicNetworkAccess = "Enabled" }
    }
  }

  sensitive_body = {
    properties = {
      administratorLoginPassword = random_password.mysql.result
    }
  }

  response_export_values = ["properties.fullyQualifiedDomainName"]
}

locals {
  mysql_fqdn = azapi_resource.mysql.output.properties.fullyQualifiedDomainName
}

# Dev+QA: Container Apps Consumption has no fixed outbound IP, so allow Azure services.
# Before production: private networking (VNet-integrated environment + private endpoint).
resource "azurerm_mysql_flexible_server_firewall_rule" "azure_services" {
  name                = "allow-azure-services"
  resource_group_name = azurerm_resource_group.rg.name
  server_name         = azapi_resource.mysql.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}

resource "azurerm_mysql_flexible_database" "schema" {
  for_each            = local.domain_services
  name                = each.value.schema
  resource_group_name = azurerm_resource_group.rg.name
  server_name         = azapi_resource.mysql.name
  charset             = "utf8mb4"
  collation           = "utf8mb4_0900_ai_ci"
}

# ---------------------------------------------------------------------------
# Container Apps
# ---------------------------------------------------------------------------

resource "azurerm_container_app_environment" "env" {
  name                       = "cae-${local.base}"
  location                   = var.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.logs.id
  tags                       = var.tags
}

locals {
  image_prefix = "${azurerm_container_registry.acr.login_server}/myclawd"
}

resource "azurerm_container_app" "service" {
  for_each = var.deploy_apps ? local.domain_services : {}

  name                         = each.key
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  tags                         = var.tags

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.apps.id]
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.apps.id
  }

  secret {
    name                = "db-password"
    key_vault_secret_id = azurerm_key_vault_secret.mysql_password.versionless_id
    identity            = azurerm_user_assigned_identity.apps.id
  }

  ingress {
    external_enabled = false # only reachable through the gateway
    target_port      = 8080
    transport        = "http"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = var.app_min_replicas
    max_replicas = 2

    container {
      name   = each.key
      image  = "${local.image_prefix}/${each.key}:${var.image_tag}"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "DB_URL"
        value = "jdbc:mysql://${local.mysql_fqdn}:3306/${each.value.schema}?sslMode=REQUIRED"
      }
      env {
        name  = "DB_USERNAME"
        value = var.mysql_admin_login
      }
      env {
        name        = "DB_PASSWORD"
        secret_name = "db-password"
      }
      env {
        name  = "AUDIT_SERVICE_URL"
        value = "http://audit-service"
      }

      startup_probe {
        transport               = "HTTP"
        port                    = 8080
        path                    = "/actuator/health/liveness"
        interval_seconds        = 10
        failure_count_threshold = 12
      }
      liveness_probe {
        transport = "HTTP"
        port      = 8080
        path      = "/actuator/health/liveness"
      }
      readiness_probe {
        transport = "HTTP"
        port      = 8080
        path      = "/actuator/health/readiness"
      }
    }
  }

  depends_on = [
    azurerm_role_assignment.apps_acr_pull,
    azurerm_role_assignment.apps_kv_secrets,
    azurerm_mysql_flexible_database.schema,
  ]
}

resource "azurerm_container_app" "gateway" {
  count = var.deploy_apps ? 1 : 0

  name                         = "api-gateway"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  tags                         = var.tags

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.apps.id]
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.apps.id
  }

  ingress {
    external_enabled = true # web app and the JavaFX desktop app call this
    target_port      = 8080
    transport        = "http"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = var.app_min_replicas
    max_replicas = 2

    container {
      name   = "api-gateway"
      image  = "${local.image_prefix}/api-gateway:${var.image_tag}"
      cpu    = 0.5
      memory = "1Gi"

      dynamic "env" {
        for_each = local.gateway_env
        content {
          name  = env.key
          value = env.value
        }
      }

      startup_probe {
        transport               = "HTTP"
        port                    = 8080
        path                    = "/actuator/health/liveness"
        interval_seconds        = 10
        failure_count_threshold = 12
      }
      readiness_probe {
        transport = "HTTP"
        port      = 8080
        path      = "/actuator/health/readiness"
      }
    }
  }

  depends_on = [azurerm_role_assignment.apps_acr_pull]
}

resource "azurerm_container_app" "web" {
  count = var.deploy_apps ? 1 : 0

  name                         = "web"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  tags                         = var.tags

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.apps.id]
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.apps.id
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    transport        = "http"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = var.app_min_replicas
    max_replicas = 2

    container {
      name   = "web"
      image  = "${local.image_prefix}/web:${var.image_tag}"
      cpu    = 0.25
      memory = "0.5Gi"

      env {
        name  = "GATEWAY_URL"
        value = "http://api-gateway"
      }

      readiness_probe {
        transport = "HTTP"
        port      = 8080
        path      = "/healthz"
      }
    }
  }

  depends_on = [azurerm_role_assignment.apps_acr_pull]
}

# ---------------------------------------------------------------------------
# Budget alert (Azure-billed spend only; the $900 cap is tracked across vendors)
# ---------------------------------------------------------------------------

resource "azurerm_consumption_budget_resource_group" "budget" {
  count             = length(var.budget_alert_emails) > 0 ? 1 : 0
  name              = "budget-${local.base}"
  resource_group_id = azurerm_resource_group.rg.id
  amount            = var.monthly_budget_usd
  time_grain        = "Monthly"

  time_period {
    start_date = formatdate("YYYY-MM-01'T'00:00:00Z", timestamp())
  }

  notification {
    enabled        = true
    threshold      = 80
    operator       = "GreaterThan"
    contact_emails = var.budget_alert_emails
  }

  notification {
    enabled        = true
    threshold      = 100
    threshold_type = "Forecasted"
    operator       = "GreaterThan"
    contact_emails = var.budget_alert_emails
  }

  lifecycle {
    ignore_changes = [time_period]
  }
}
