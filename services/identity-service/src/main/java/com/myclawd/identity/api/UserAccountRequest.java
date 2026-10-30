package com.myclawd.identity.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a user. */
public record UserAccountRequest(
        @NotBlank String fullName,
        @NotBlank String email,
        @NotBlank String role,
        @NotBlank String accountType) {
}
