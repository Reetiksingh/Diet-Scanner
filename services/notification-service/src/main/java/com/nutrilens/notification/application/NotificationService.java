package com.nutrilens.notification.application;

import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.security.SecuritySupport;
import com.nutrilens.notification.api.dto.NotificationDtos.NotificationList;
import com.nutrilens.notification.api.dto.NotificationDtos.NotificationResponse;
import com.nutrilens.notification.domain.Notification;
import com.nutrilens.notification.domain.NotificationPreference;
import com.nutrilens.notification.infrastructure.NotificationPreferenceRepository;
import com.nutrilens.notification.infrastructure.NotificationRepository;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationService(NotificationRepository notificationRepository, NotificationPreferenceRepository preferenceRepository,
                               SimpMessagingTemplate messagingTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.messagingTemplate = messagingTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = {KafkaTopics.ACHIEVEMENT_UNLOCKED, KafkaTopics.RECOMMENDATION_GENERATED, KafkaTopics.WEEKLY_REPORT_GENERATED, KafkaTopics.CHALLENGE_COMPLETED}, groupId = "notification-service")
    @Transactional
    public void onEvent(EventEnvelope<?> envelope) {
        LinkedHashMap<?, ?> payload = (LinkedHashMap<?, ?>) envelope.payload();
        UUID userId = UUID.fromString(String.valueOf(payload.get("userId")));
        Object titleValue = payload.get("title") == null ? payload.get("name") : payload.get("title");
        String title = titleValue == null ? envelope.eventType() : String.valueOf(titleValue);
        Notification notification = notificationRepository.save(new Notification(userId, envelope.eventType(), title,
                bodyFor(envelope.eventType(), title), "{\"sourceEventId\":\"" + envelope.eventId() + "\"}"));
        NotificationResponse response = toResponse(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, response);
        kafkaTemplate.send(KafkaTopics.NOTIFICATION_CREATED, userId.toString(), EventEnvelope.of(
                "NotificationCreatedEvent",
                "notification-service",
                envelope.correlationId(),
                new Events.NotificationCreatedEvent(userId, notification.getId(), notification.getType(), notification.getTitle())
        ));
    }

    @Transactional(readOnly = true)
    public NotificationList inbox() {
        UUID userId = SecuritySupport.currentUserId();
        return new NotificationList(notificationRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList());
    }

    @Transactional
    public void markRead(UUID id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.markRead();
    }

    @Transactional
    public NotificationPreference preferences() {
        UUID userId = SecuritySupport.currentUserId();
        return preferenceRepository.findById(userId).orElseGet(() -> preferenceRepository.save(new NotificationPreference(userId)));
    }

    private String bodyFor(String eventType, String title) {
        if (eventType.contains("Achievement")) {
            return "Achievement unlocked: " + title;
        }
        if (eventType.contains("Recommendation")) {
            return "New nutrition recommendation is ready.";
        }
        if (eventType.contains("Weekly")) {
            return "Your weekly nutrition report is ready.";
        }
        return "NutriLens update: " + title;
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getType(), notification.getTitle(), notification.getBody(), notification.isRead(), notification.getCreatedAt());
    }
}
