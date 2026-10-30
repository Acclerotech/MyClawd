package com.myclawd.documents.api;

import com.myclawd.documents.domain.DocumentRecord;
import java.time.Instant;

public record DocumentRecordResponse(
        Long id,
        Long workOrderId,
        String fileName,
        String storageUri,
        String sha256,
        String status,
        Instant createdAt) {

    public static DocumentRecordResponse from(DocumentRecord e) {
        return new DocumentRecordResponse(e.getId(), e.getWorkOrderId(), e.getFileName(), e.getStorageUri(), e.getSha256(), e.getStatus(), e.getCreatedAt());
    }
}
