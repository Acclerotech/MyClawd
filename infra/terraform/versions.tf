terraform {
  required_version = ">= 1.9"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
    azapi = {
      source  = "Azure/azapi"
      version = "~> 2.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }

  # State lives in Azure Storage (Tech Stack: "Terraform (state in Azure Storage)").
  # Values are passed at init time: see docs/azure-setup.md and .github/workflows/deploy.yml.
  backend "azurerm" {}
}

provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy = false
    }
  }
  # In GitHub Actions authentication is OIDC (ARM_USE_OIDC=true); locally `az login`.
}

provider "azapi" {}
