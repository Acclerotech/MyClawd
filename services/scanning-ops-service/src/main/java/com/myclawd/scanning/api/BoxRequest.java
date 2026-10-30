package com.myclawd.scanning.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a box. */
public record BoxRequest(
        @NotNull Long workOrderId,
        @NotBlank String boxNumber,
        @NotBlank String status) {
}
