# The 12 microservices (Estimates workbook > Microservices Map) plus the React web app.
# Each domain service owns exactly one MySQL schema.
locals {
  domain_services = {
    "identity-service"                = { schema = "identity" }
    "audit-service"                   = { schema = "audit" }
    "entity-trust-service"            = { schema = "entity_trust" }
    "registration-authority-service"  = { schema = "registration_authority" }
    "signature-service"               = { schema = "signature" }
    "legal-consent-service"           = { schema = "legal_consent" }
    "work-order-service"              = { schema = "work_order" }
    "scanning-ops-service"            = { schema = "scanning_ops" }
    "documents-transfer-service"      = { schema = "documents_transfer" }
    "reports-service"                 = { schema = "reports" }
    "dashboard-notifications-service" = { schema = "dashboard" }
  }

  # Gateway env: one <NAME>_SERVICE_URL per domain service, pointing at its internal app.
  gateway_env = {
    for name, _ in local.domain_services :
    "${upper(replace(trimsuffix(name, "-service"), "-", "_"))}_SERVICE_URL" => "http://${name}"
  }
}
