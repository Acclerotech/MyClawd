package com.myclawd.reports.api;

import com.myclawd.reports.domain.ReportJob;
import java.time.Instant;

public record ReportJobResponse(
        Long id,
        String reportType,
        Long workOrderId,
        String requestedBy,
        String status,
        String blobUri,
        Instant createdAt) {

    public static ReportJobResponse from(ReportJob e) {
        return new ReportJobResponse(e.getId(), e.getReportType(), e.getWorkOrderId(), e.getRequestedBy(), e.getStatus(), e.getBlobUri(), e.getCreatedAt());
    }
}
