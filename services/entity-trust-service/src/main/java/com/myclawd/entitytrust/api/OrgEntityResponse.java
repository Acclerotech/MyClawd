package com.myclawd.entitytrust.api;

import com.myclawd.entitytrust.domain.OrgEntity;
import java.time.Instant;

public record OrgEntityResponse(
        Long id,
        String name,
        String entityType,
        String country,
        Integer trustLevel,
        Instant createdAt) {

    public static OrgEntityResponse from(OrgEntity e) {
        return new OrgEntityResponse(e.getId(), e.getName(), e.getEntityType(), e.getCountry(), e.getTrustLevel(), e.getCreatedAt());
    }
}
