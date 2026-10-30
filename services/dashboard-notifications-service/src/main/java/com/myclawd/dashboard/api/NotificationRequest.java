package com.myclawd.dashboard.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Create request for a notification. */
public record NotificationRequest(
        @NotNull Long recipientUserId,
        @NotBlank String title,
        String body) {
}
