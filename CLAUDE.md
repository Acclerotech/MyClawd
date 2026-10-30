# Notes for Claude Code in this repo

- Read `docs/architecture.md` first. The clickable prototype and `MyClawd_TestCases.docx` are the spec.
- Java 21, Spring Boot 4.1.x, Spring Cloud 2025.1.x. Build with `mvn -B verify`; web with `cd web && npm test && npm run build`.
- Tests first: write the failing unit test, then the code. Domain logic lives in `domain/` as plain Java with no Spring.
- A service may only touch its own schema. New tables go in a new Flyway file `V<n>__<what>.sql` in that service; never edit an applied migration.
- Every state change calls `AuditRecorder.record(actor, event, evidence)` in the same transaction.
- Every user-visible web string goes in both `web/src/locales/en.json` and `fr.json`. Use interpolation, never string concatenation.
- No client documents, names or other personal data in code, fixtures, logs or prompts. Use obviously fake seed data.
- Keep PRs small and use the PR template checklist.
