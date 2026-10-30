package com.myclawd.dashboard.api;

import com.myclawd.dashboard.domain.Notification;
import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long recipientUserId,
        String title,
        String body,
        Instant readAt,
        Instant createdAt) {

    public static NotificationResponse from(Notification e) {
        return new NotificationResponse(e.getId(), e.getRecipientUserId(), e.getTitle(), e.getBody(), e.getReadAt(), e.getCreatedAt());
    }
}
