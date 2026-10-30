package com.myclawd.ra.api;

import com.myclawd.ra.domain.RaCase;
import java.time.Instant;

public record RaCaseResponse(
        Long id,
        String subjectType,
        Long subjectId,
        Integer raStep,
        String status,
        String certRef,
        Instant createdAt) {

    public static RaCaseResponse from(RaCase e) {
        return new RaCaseResponse(e.getId(), e.getSubjectType(), e.getSubjectId(), e.getRaStep(), e.getStatus(), e.getCertRef(), e.getCreatedAt());
    }
}
