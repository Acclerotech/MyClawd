CREATE TABLE ra_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_type VARCHAR(20) NOT NULL,
    subject_id BIGINT NOT NULL,
    ra_step INT NOT NULL,
    status VARCHAR(40) NOT NULL,
    cert_ref VARCHAR(80),
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
