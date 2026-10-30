CREATE TABLE work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference VARCHAR(40) NOT NULL UNIQUE,
    client_entity_id BIGINT NOT NULL,
    provider_entity_id BIGINT NOT NULL,
    client_contact_user_id BIGINT,
    assigned_manager_id BIGINT,
    current_step INT NOT NULL,
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
