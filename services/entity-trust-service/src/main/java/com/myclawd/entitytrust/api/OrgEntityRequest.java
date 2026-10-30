package com.myclawd.entitytrust.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a entity. */
public record OrgEntityRequest(
        @NotBlank String name,
        @NotBlank String entityType,
        @NotBlank String country) {
}
