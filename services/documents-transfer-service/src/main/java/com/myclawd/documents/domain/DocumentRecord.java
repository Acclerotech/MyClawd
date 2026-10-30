package com.myclawd.documents.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "document_record")
public class DocumentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workOrderId;

    private String fileName;

    private String storageUri;

    private String sha256;

    private String status;

    private Instant createdAt;

    protected DocumentRecord() {
        // for JPA
    }

    public DocumentRecord(Long workOrderId, String fileName, String storageUri, String sha256, String status) {
        this.workOrderId = workOrderId;
        this.fileName = fileName;
        this.storageUri = storageUri;
        this.sha256 = sha256;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStorageUri() {
        return storageUri;
    }

    public String getSha256() {
        return sha256;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
