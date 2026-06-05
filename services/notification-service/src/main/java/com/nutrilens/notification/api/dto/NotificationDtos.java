package com.nutrilens.notification.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class NotificationDtos {
    private NotificationDtos() {
    }

    public record NotificationResponse(UUID id, String type, String title, String body, boolean read, Instant createdAt) {
    }

    public record NotificationList(List<NotificationResponse> notifications) {
    }
}

