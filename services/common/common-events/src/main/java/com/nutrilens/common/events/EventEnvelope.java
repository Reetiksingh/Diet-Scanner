package com.nutrilens.common.events;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        String correlationId,
        String producer,
        T payload
) {
    public static <T> EventEnvelope<T> of(String eventType, String producer, String correlationId, T payload) {
        return new EventEnvelope<>(UUID.randomUUID(), eventType, 1, Instant.now(), correlationId, producer, payload);
    }
}

