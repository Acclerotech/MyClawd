package com.myclawd.audit.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a audit event. */
public record AuditEventRequest(
        @NotBlank String source,
        @NotBlank String actor,
        @NotBlank String event,
        String evidence,
        String correlationId,
        String occurredAt) {
}
