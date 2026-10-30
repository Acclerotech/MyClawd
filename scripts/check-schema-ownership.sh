#!/usr/bin/env bash
# Dev Principle 4: every service owns its schema; no service reads another's tables.
# Fails if a service's migrations or Java code reference another service's schema
# (e.g. "work_order.box" inside scanning-ops-service).
set -euo pipefail
cd "$(dirname "$0")/.."

declare -A SCHEMA=(
  [identity-service]=identity
  [audit-service]=audit
  [entity-trust-service]=entity_trust
  [registration-authority-service]=registration_authority
  [signature-service]=signature
  [legal-consent-service]=legal_consent
  [work-order-service]=work_order
  [scanning-ops-service]=scanning_ops
  [documents-transfer-service]=documents_transfer
  [reports-service]=reports
  [dashboard-notifications-service]=dashboard
)

fail=0
for svc in "${!SCHEMA[@]}"; do
  for other in "${!SCHEMA[@]}"; do
    [[ "$svc" == "$other" ]] && continue
    schema="${SCHEMA[$other]}"
    if grep -rIniE "(from|join|into|update|table)[[:space:]]+\`?${schema}\`?\." "services/$svc/src/main" ; then
      echo "::error::$svc references schema '$schema' owned by $other"
      fail=1
    fi
  done
done
[[ $fail -eq 0 ]] && echo "Schema ownership check passed."
exit $fail
