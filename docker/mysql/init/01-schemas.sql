-- One schema per service (Dev Principle 4). Local only: a single app user for convenience.
-- In Azure each service gets its own login limited to its own schema (see docs/azure-setup.md).
CREATE DATABASE IF NOT EXISTS identity;
GRANT ALL PRIVILEGES ON identity.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS audit;
GRANT ALL PRIVILEGES ON audit.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS entity_trust;
GRANT ALL PRIVILEGES ON entity_trust.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS registration_authority;
GRANT ALL PRIVILEGES ON registration_authority.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS signature;
GRANT ALL PRIVILEGES ON signature.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS legal_consent;
GRANT ALL PRIVILEGES ON legal_consent.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS work_order;
GRANT ALL PRIVILEGES ON work_order.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS scanning_ops;
GRANT ALL PRIVILEGES ON scanning_ops.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS documents_transfer;
GRANT ALL PRIVILEGES ON documents_transfer.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS reports;
GRANT ALL PRIVILEGES ON reports.* TO 'myclawd'@'%';
CREATE DATABASE IF NOT EXISTS dashboard;
GRANT ALL PRIVILEGES ON dashboard.* TO 'myclawd'@'%';
FLUSH PRIVILEGES;
