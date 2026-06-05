package com.nutrilens.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events", schema = "auth")
public class AuthOutboxEvent {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String topic;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(nullable = false, columnDefinition = "text")
    private String payload;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "published_at")
    private Instant publishedAt;

    protected AuthOutboxEvent() {
    }

    public AuthOutboxEvent(String topic, String eventType, String payload) {
        this.id = UUID.randomUUID();
        this.topic = topic;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public void published() {
        this.publishedAt = Instant.now();
    }
}

