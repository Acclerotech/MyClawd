variable "environment" {
  description = "Environment name used in resource names (one shared Dev+QA environment)."
  type        = string
  default     = "devqa"
}

variable "location" {
  description = "Azure region. All client data, backups and logs stay in Canada."
  type        = string
  validation {
    condition     = contains(["canadacentral", "canadaeast"], var.location)
    error_message = "Data residency: location must be canadacentral or canadaeast."
  }
}

variable "name_prefix" {
  description = "Short prefix for resource names (lowercase letters and digits)."
  type        = string
  default     = "myclawd"
}

variable "deploy_apps" {
  description = "false on the very first run (creates the registry so images can be pushed); true afterwards."
  type        = bool
  default     = true
}

variable "image_tag" {
  description = "Container image tag to deploy (the CI sets the git commit SHA)."
  type        = string
  default     = "latest"
}

variable "mysql_sku" {
  description = "Burstable MySQL SKU for Dev+QA."
  type        = string
  default     = "B_Standard_B1ms"
}

variable "mysql_admin_login" {
  type    = string
  default = "myclawdadmin"
}

variable "app_min_replicas" {
  description = "0 = scale to zero outside QA windows (keeps the Container Apps cost line)."
  type        = number
  default     = 0
}

variable "monthly_budget_usd" {
  description = "Azure-billed budget alert for this resource group (Procurement Handover: Azure budget)."
  type        = number
  default     = 230
}

variable "budget_alert_emails" {
  type    = list(string)
  default = []
}

variable "tags" {
  type    = map(string)
  default = {
    product   = "MyClawd"
    managedBy = "terraform"
  }
}
