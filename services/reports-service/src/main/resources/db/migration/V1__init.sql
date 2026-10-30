CREATE TABLE report_job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_type VARCHAR(60) NOT NULL,
    work_order_id BIGINT,
    requested_by VARCHAR(200) NOT NULL,
    status VARCHAR(30) NOT NULL,
    blob_uri VARCHAR(1000),
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
