package com.myclawd.platform.audit;

import com.myclawd.platform.web.CorrelationIdFilter;
import java.sql.Timestamp;
import java.time.Instant;
import org.slf4j.MDC;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * "Audit by default" (Dev Principle 11): every state change calls {@link #record} inside
 * the same transaction as the change. The row lands in this service's own
 * {@code outbox_event} table and {@link AuditOutboxRelay} forwards it to the audit service,
 * so an event is never lost if the audit service is briefly unavailable.
 * In production the relay target becomes an Azure Service Bus topic.
 */
@Component
public class AuditRecorder {

    private final JdbcClient jdbc;

    public AuditRecorder(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void record(String actor, String event, String evidence) {
        jdbc.sql("""
                INSERT INTO outbox_event (actor, event, evidence, correlation_id, occurred_at)
                VALUES (:actor, :event, :evidence, :correlationId, :occurredAt)
                """)
                .param("actor", actor)
                .param("event", event)
                .param("evidence", evidence)
                .param("correlationId", MDC.get(CorrelationIdFilter.MDC_KEY))
                .param("occurredAt", Timestamp.from(Instant.now()))
                .update();
    }
}
