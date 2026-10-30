CREATE TABLE org_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    entity_type VARCHAR(30) NOT NULL,
    country VARCHAR(60) NOT NULL,
    trust_level INT NOT NULL,
    created_at DATETIME(6) NOT NULL
);

-- Transactional outbox: audit events written in the same transaction as the change,
-- forwarded to the audit service by AuditOutboxRelay (platform-common).
CREATE TABLE outbox_event (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor          VARCHAR(200) NOT NULL,
    event          VARCHAR(300) NOT NULL,
    evidence       TEXT,
    correlation_id VARCHAR(64),
    occurred_at    DATETIME(6) NOT NULL,
    sent_at        DATETIME(6),
    INDEX ix_outbox_unsent (sent_at, id)
);
