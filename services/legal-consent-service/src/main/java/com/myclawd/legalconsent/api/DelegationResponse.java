package com.myclawd.legalconsent.api;

import com.myclawd.legalconsent.domain.Delegation;
import java.time.Instant;

public record DelegationResponse(
        Long id,
        Long clientEntityId,
        Long providerEntityId,
        Long delegateUserId,
        Long grantedByUserId,
        String status,
        Instant revokedAt,
        Instant createdAt) {

    public static DelegationResponse from(Delegation e) {
        return new DelegationResponse(e.getId(), e.getClientEntityId(), e.getProviderEntityId(), e.getDelegateUserId(), e.getGrantedByUserId(), e.getStatus(), e.getRevokedAt(), e.getCreatedAt());
    }
}
