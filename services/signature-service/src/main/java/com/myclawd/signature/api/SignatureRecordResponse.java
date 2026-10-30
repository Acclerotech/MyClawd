package com.myclawd.signature.api;

import com.myclawd.signature.domain.SignatureRecord;
import java.time.Instant;

public record SignatureRecordResponse(
        Long id,
        Long userId,
        String documentRef,
        String method,
        Instant createdAt) {

    public static SignatureRecordResponse from(SignatureRecord e) {
        return new SignatureRecordResponse(e.getId(), e.getUserId(), e.getDocumentRef(), e.getMethod(), e.getCreatedAt());
    }
}
