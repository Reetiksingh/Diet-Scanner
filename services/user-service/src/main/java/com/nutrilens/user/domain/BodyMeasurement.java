package com.nutrilens.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "body_measurements", schema = "users")
public class BodyMeasurement {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "measured_on", nullable = false)
    private LocalDate measuredOn;
    @Column(name = "weight_kg", nullable = false)
    private BigDecimal weightKg;
    @Column(name = "body_fat_percent")
    private BigDecimal bodyFatPercent;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected BodyMeasurement() {
    }

    public BodyMeasurement(UUID userId, LocalDate measuredOn, BigDecimal weightKg, BigDecimal bodyFatPercent) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.measuredOn = measuredOn;
        this.weightKg = weightKg;
        this.bodyFatPercent = bodyFatPercent;
        this.createdAt = Instant.now();
    }
}

