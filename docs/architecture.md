# Architecture

Source documents: MyClawd Build Plan v1.1 (§3.5), Estimates workbook v0.2 (Microservices Map, Tech Stack, Dev Principles, Decisions), CLAWD Procurement Handover v1.2.

## Shape

```
 Browser (React)            JavaFX desktop app
        │                           │
        ▼                           │
   web (nginx) ──/api──┐            │
                       ▼            ▼
                    api-gateway (Spring Cloud Gateway)   ← external ingress
                       │  routes /api/<service>/**
   ┌──────────┬────────┼─────────┬──────────────┬─────────── … (11 services, internal ingress)
   ▼          ▼        ▼         ▼              ▼
 identity  entity-   legal-   work-order    scanning-ops  …
           trust     consent
   │          │        │         │              │
   └── outbox_event ───┴─────────┴──────────────┘
                       │  AuditOutboxRelay (every 5 s, retries)
                       ▼
                  audit-service (append-only, hash-chained)

 MySQL Flexible Server 8.4: one schema per service, no cross-schema reads
```

## Rules the code already enforces

| Rule | Where |
| --- | --- |
| Each service owns its schema | `spring.datasource.url` points at its own schema; Flyway migrations live in the service; `scripts/check-schema-ownership.sh` fails CI on cross-schema SQL. |
| Audit by default | `AuditRecorder.record(...)` writes to the service's `outbox_event` table in the same transaction as the change; `AuditOutboxRelay` forwards to `audit-service`. In production the relay target becomes an Azure Service Bus topic. |
| Tamper-evident audit | `audit-service` links every event to the previous one with SHA-256 (`HashChain`). `GET /api/audit/events/verify` re-checks the whole chain. `prev_hash` is unique, so two concurrent appends can't fork it. |
| One error format | `ApiExceptionHandler`: 400 with field errors, 404, 409 for broken business rules. |
| Traceability | `X-Correlation-Id` is accepted or created on every request and put in the log context. |
| Bilingual | Web strings live only in `web/src/locales/en.json` and `fr.json`; interpolation (`Step {{step}} of 7`) replaces the prototype's text-node matching. Dates follow the UI language. |

## Business rules already implemented (with tests)

| Rule (from the prototype) | Service | Code |
| --- | --- | --- |
| 8-character user code: 4 letters + 4 digits, unique | identity | `UserCodeGenerator` |
| Work order moves through the 7-step tracker; cannot pass step 7; "Step N completed" audit event | work-order | `WorkOrder.advanceStep()` |
| One active Delegation of Authority per client/provider pair; a new grant revokes the old one; revoke is final | legal-consent | `DelegationService` |
| Box sub-step timer: employee required before start; every Start/Resume → Pause/Complete is its own session with real timestamps; Reset clears | scanning-ops | `SubStepTimer` (domain only; persistence is the next slice) |
| Append-only, hash-chained audit log | audit | `HashChain` |

Every other service has its primary record (create, list, get) with validation, an audit event on create, and controller tests — the starting point for its phase.

## Not yet in the skeleton (planned)

| Item | Phase / decision |
| --- | --- |
| Entra ID + Entra External ID sign-in; token validation at the gateway; role checks in identity-service. `X-Actor` header is a placeholder until then. | P1, D5 |
| Per-service MySQL logins limited to their own schema (today all services use the admin login from Key Vault) | Before QA sign-off |
| OpenTelemetry → Application Insights | P1 |
| Testcontainers integration tests and Pact contract tests | P1 |
| Azure Blob Storage for files; signed download links | P3 |
| Service Bus topic for audit/notification events | Before production |
| Private networking (VNet, private endpoint for MySQL) | Before production |
| JavaFX standalone processing app | P3–P4, D8 |
