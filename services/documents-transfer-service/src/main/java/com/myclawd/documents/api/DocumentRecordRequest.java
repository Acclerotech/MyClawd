package com.myclawd.documents.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a document. */
public record DocumentRecordRequest(
        @NotNull Long workOrderId,
        @NotBlank String fileName,
        String storageUri,
        String sha256,
        @NotBlank String status) {
}
