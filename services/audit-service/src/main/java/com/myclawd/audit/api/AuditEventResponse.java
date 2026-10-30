package com.myclawd.audit.api;

import com.myclawd.audit.domain.AuditEvent;
import java.time.Instant;

public record AuditEventResponse(
        Long id,
        String source,
        String actor,
        String event,
        String evidence,
        String correlationId,
        String occurredAt,
        String prevHash,
        String hash,
        Instant createdAt) {

    public static AuditEventResponse from(AuditEvent e) {
        return new AuditEventResponse(e.getId(), e.getSource(), e.getActor(), e.getEvent(), e.getEvidence(), e.getCorrelationId(), e.getOccurredAt(), e.getPrevHash(), e.getHash(), e.getCreatedAt());
    }
}
