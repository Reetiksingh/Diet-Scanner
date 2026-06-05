package com.nutrilens.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "notifications")
public class Notification {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    private String type;
    private String title;
    @Column(columnDefinition = "text")
    private String body;
    @Column(columnDefinition = "text")
    private String payload;
    @Column(name = "read_at")
    private Instant readAt;
    @Column(name = "delivered_at")
    private Instant deliveredAt;
    @Column(name = "created_at")
    private Instant createdAt;

    protected Notification() {
    }

    public Notification(UUID userId, String type, String title, String body, String payload) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public void markRead() {
        this.readAt = Instant.now();
    }

    public void markDelivered() {
        this.deliveredAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public boolean isRead() { return readAt != null; }
    public Instant getCreatedAt() { return createdAt; }
}

