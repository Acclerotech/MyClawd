## What and why

<!-- Story / prototype screen / TC ID this implements (Dev Principle 3) -->

## Definition of Done

- [ ] Failing test written first; unit tests pass (domain coverage ≥ 80%)
- [ ] OpenAPI contract updated and reviewed (contract-first)
- [ ] Every state change writes an audit event (`AuditRecorder.record`)
- [ ] EN **and** FR strings added (web: `en.json` + `fr.json`); no hard-coded text
- [ ] Service only touches its own schema
- [ ] No secrets, client documents or personal data in code, logs or AI prompts
- [ ] Reviewed by a named person
