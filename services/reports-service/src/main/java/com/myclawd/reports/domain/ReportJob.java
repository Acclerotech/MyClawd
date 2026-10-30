package com.myclawd.reports.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "report_job")
public class ReportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType;

    private Long workOrderId;

    private String requestedBy;

    private String status;

    private String blobUri;

    private Instant createdAt;

    protected ReportJob() {
        // for JPA
    }

    public ReportJob(String reportType, Long workOrderId, String requestedBy, String blobUri) {
        this.reportType = reportType;
        this.workOrderId = workOrderId;
        this.requestedBy = requestedBy;
        this.status = "Queued";
        this.blobUri = blobUri;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getReportType() {
        return reportType;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getStatus() {
        return status;
    }

    public String getBlobUri() {
        return blobUri;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
