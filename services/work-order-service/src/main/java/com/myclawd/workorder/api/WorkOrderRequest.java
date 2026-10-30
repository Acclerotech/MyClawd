package com.myclawd.workorder.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a work order. */
public record WorkOrderRequest(
        @NotBlank String reference,
        @NotNull Long clientEntityId,
        @NotNull Long providerEntityId,
        Long clientContactUserId,
        Long assignedManagerId) {
}
