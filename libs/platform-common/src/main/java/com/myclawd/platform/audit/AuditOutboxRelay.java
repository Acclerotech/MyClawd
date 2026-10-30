package com.myclawd.platform.audit;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Forwards unsent outbox rows to the audit service and marks them sent. Retries on the next tick. */
@Component
@ConditionalOnProperty(name = "myclawd.audit.relay-enabled", havingValue = "true", matchIfMissing = true)
public class AuditOutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(AuditOutboxRelay.class);

    private final JdbcClient jdbc;
    private final RestClient audit;
    private final String source;

    public AuditOutboxRelay(JdbcClient jdbc,
                            @Value("${myclawd.audit.service-url:http://localhost:8082}") String auditUrl,
                            @Value("${spring.application.name}") String source) {
        this.jdbc = jdbc;
        this.audit = RestClient.create(auditUrl);
        this.source = source;
    }

    @Scheduled(fixedDelayString = "${myclawd.audit.relay-interval-ms:5000}")
    public void relay() {
        List<Map<String, Object>> rows = jdbc.sql("""
                SELECT id, actor, event, evidence, correlation_id, occurred_at
                FROM outbox_event WHERE sent_at IS NULL ORDER BY id LIMIT 50
                """).query().listOfRows();
        for (Map<String, Object> row : rows) {
            try {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("source", source);
                body.put("actor", row.get("actor"));
                body.put("event", row.get("event"));
                body.put("evidence", row.get("evidence"));
                body.put("correlationId", row.get("correlation_id"));
                body.put("occurredAt", String.valueOf(row.get("occurred_at")));
                audit.post().uri("/api/audit/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .toBodilessEntity();
                jdbc.sql("UPDATE outbox_event SET sent_at = :now WHERE id = :id")
                        .param("now", Timestamp.from(Instant.now()))
                        .param("id", row.get("id"))
                        .update();
            } catch (RuntimeException ex) {
                log.warn("Audit relay failed for outbox row {}; will retry: {}", row.get("id"), ex.getMessage());
                return; // keep ordering: stop at the first failure
            }
        }
    }
}
