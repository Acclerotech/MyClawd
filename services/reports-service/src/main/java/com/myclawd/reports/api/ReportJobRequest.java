package com.myclawd.reports.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a report job. */
public record ReportJobRequest(
        @NotBlank String reportType,
        Long workOrderId,
        @NotBlank String requestedBy,
        String blobUri) {
}
