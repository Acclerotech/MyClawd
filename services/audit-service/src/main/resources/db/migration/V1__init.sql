CREATE TABLE audit_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(80) NOT NULL,
    actor VARCHAR(200) NOT NULL,
    event VARCHAR(300) NOT NULL,
    evidence TEXT,
    correlation_id VARCHAR(64),
    occurred_at VARCHAR(40),
    prev_hash CHAR(64) NOT NULL UNIQUE,  -- a second append on the same tip fails: no forks
    hash CHAR(64) NOT NULL UNIQUE,
    created_at DATETIME(6) NOT NULL
);
