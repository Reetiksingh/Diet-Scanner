package com.nutrilens.notification.api;

import com.nutrilens.notification.api.dto.NotificationDtos.NotificationList;
import com.nutrilens.notification.application.NotificationService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public NotificationList inbox() {
        return notificationService.inbox();
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable UUID id) {
        notificationService.markRead(id);
    }

    @PatchMapping("/read-all")
    public void readAll() {
        notificationService.inbox().notifications().forEach(item -> notificationService.markRead(item.id()));
    }
}

