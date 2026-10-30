package com.myclawd.legalconsent.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a delegation. */
public record DelegationRequest(
        @NotNull Long clientEntityId,
        @NotNull Long providerEntityId,
        @NotNull Long delegateUserId,
        @NotNull Long grantedByUserId) {
}
