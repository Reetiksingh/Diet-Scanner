package com.nutrilens.analytics.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "nutrition_insights", schema = "analytics")
public class NutritionInsight {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "insight_type")
    private String insightType;
    private String title;
    @Column(columnDefinition = "text")
    private String message;
    @Column(columnDefinition = "text")
    private String evidence;
    private String severity;
    @Column(name = "period_start")
    private LocalDate periodStart;
    @Column(name = "period_end")
    private LocalDate periodEnd;
    @Column(name = "created_at")
    private Instant createdAt;

    protected NutritionInsight() {
    }

    public NutritionInsight(UUID userId, String insightType, String title, String message, String evidence, String severity, LocalDate periodStart, LocalDate periodEnd) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.insightType = insightType;
        this.title = title;
        this.message = message;
        this.evidence = evidence;
        this.severity = severity;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getInsightType() { return insightType; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getEvidence() { return evidence; }
    public String getSeverity() { return severity; }
}

