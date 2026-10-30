output "resource_group" {
  value = azurerm_resource_group.rg.name
}

output "acr_name" {
  value = azurerm_container_registry.acr.name
}

output "acr_login_server" {
  value = azurerm_container_registry.acr.login_server
}

output "mysql_fqdn" {
  value = local.mysql_fqdn
}

output "web_url" {
  value = var.deploy_apps ? "https://${azurerm_container_app.web[0].ingress[0].fqdn}" : null
}

output "gateway_url" {
  value = var.deploy_apps ? "https://${azurerm_container_app.gateway[0].ingress[0].fqdn}" : null
}
