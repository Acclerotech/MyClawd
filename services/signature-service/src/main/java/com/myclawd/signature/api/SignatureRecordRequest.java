package com.myclawd.signature.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a signature. */
public record SignatureRecordRequest(
        @NotNull Long userId,
        @NotBlank String documentRef,
        @NotBlank String method) {
}
