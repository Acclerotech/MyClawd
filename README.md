# MyClawd

UnikSign (digital identity, trust levels, e-signature) and DocuTrail (scanning work orders with a full chain of custody), built as microservices on Azure.

This repository is the **production skeleton** that follows the MyClawd Build Plan v1.1 and the Estimates workbook: 12 Spring Boot services, a React web app, Terraform for Azure, and GitHub Actions for CI/CD. The clickable HTML prototype remains the specification (Dev Principle 3).

## What's in here

| Path | What |
| --- | --- |
| `services/api-gateway` | Spring Cloud Gateway — single entry point for the web and desktop apps. Routing only. |
| `services/*-service` | 11 domain services. Each owns one MySQL schema, has its own Flyway migrations, REST API (OpenAPI at `/api/<service>/swagger-ui.html`), audit outbox and container app. |
| `libs/platform-common` | Correlation IDs, one error format (RFC 9457), and the audit outbox (`AuditRecorder`). |
| `web` | React + TypeScript + Ant Design (Vite), EN/FR with react-i18next. Served by nginx, which proxies `/api` to the gateway. |
| `infra/terraform` | Azure: Container Apps (one app per service, scale to zero), MySQL Flexible Server 8.4, Container Registry, Key Vault, Log Analytics, budget alert. |
| `.github/workflows` | `ci.yml` (build, tests, schema-ownership check, SBOM, web, Terraform validate), `codeql.yml`, `deploy.yml` (OIDC to Azure). |
| `docs` | [Architecture](docs/architecture.md) · [Azure setup](docs/azure-setup.md) |

## Services

| # | Service | Port (local) | API base | Schema |
| --- | --- | --- | --- | --- |
| 1 | api-gateway | 8080 | `/api/**` | — |
| 2 | identity-service | 8081 | `/api/identity` | `identity` |
| 3 | audit-service | 8082 | `/api/audit` | `audit` |
| 4 | entity-trust-service | 8083 | `/api/entity-trust` | `entity_trust` |
| 5 | registration-authority-service | 8084 | `/api/registration-authority` | `registration_authority` |
| 6 | signature-service | 8085 | `/api/signature` | `signature` |
| 7 | legal-consent-service | 8086 | `/api/legal-consent` | `legal_consent` |
| 8 | work-order-service | 8087 | `/api/work-order` | `work_order` |
| 9 | scanning-ops-service | 8088 | `/api/scanning-ops` | `scanning_ops` |
| 10 | documents-transfer-service | 8089 | `/api/documents-transfer` | `documents_transfer` |
| 11 | reports-service | 8090 | `/api/reports` | `reports` |
| 12 | dashboard-notifications-service | 8091 | `/api/dashboard-notifications` | `dashboard` |

The JavaFX standalone processing app (#13 in the Microservices Map) is a separate client and is not in this repository yet.

## Run locally

Requirements: Java 21, Maven 3.9+, Node 22, Docker.

```bash
mvn -B package -DskipTests        # build all service jars
docker compose up --build         # MySQL 8.4 + 12 services + web
```

- Web app: http://localhost:5173
- Gateway: http://localhost:8080 — e.g. `curl localhost:8080/api/work-order/work-orders`
- Each service's Swagger UI: http://localhost:8080/api/work-order/swagger-ui.html (swap the service name)

Web app only, against a running gateway: `cd web && npm install && npm run dev`.

## Test

```bash
mvn -B verify          # all Java unit tests
cd web && npm test     # web tests
./scripts/check-schema-ownership.sh
```

## Deploy

Every merge to `main` that passes CI is built into images, pushed to the environment's registry and rolled out to Azure Container Apps by `deploy.yml`. One-time Azure setup (state storage, OIDC identity, GitHub variables) is in [docs/azure-setup.md](docs/azure-setup.md). Until those variables exist, the deploy job skips itself.

## Working rules

The 16 Dev Principles in the Estimates workbook apply; the pull-request template carries the Definition of Done. In short: tests first, contract first, one schema per service, audit every state change, EN and FR together, secrets only in Key Vault, small PRs to `main`.
