package com.myclawd.ra.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a ra case. */
public record RaCaseRequest(
        @NotBlank String subjectType,
        @NotNull Long subjectId,
        String certRef) {
}
