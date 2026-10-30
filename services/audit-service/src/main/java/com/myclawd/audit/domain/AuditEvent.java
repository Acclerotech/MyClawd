package com.myclawd.audit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "audit_event")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;

    private String actor;

    private String event;

    private String evidence;

    private String correlationId;

    private String occurredAt;

    private String prevHash;

    private String hash;

    private Instant createdAt;

    protected AuditEvent() {
        // for JPA
    }

    public AuditEvent(String source, String actor, String event, String evidence, String correlationId, String occurredAt) {
        this.source = source;
        this.actor = actor;
        this.event = event;
        this.evidence = evidence;
        this.correlationId = correlationId;
        this.occurredAt = occurredAt;
        this.prevHash = null;
        this.hash = null;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getActor() {
        return actor;
    }

    public String getEvent() {
        return event;
    }

    public String getEvidence() {
        return evidence;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getOccurredAt() {
        return occurredAt;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public String getHash() {
        return hash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
