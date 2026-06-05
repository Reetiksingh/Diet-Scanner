package com.nutrilens.recommendation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recommendations", schema = "recommendations")
public class Recommendation {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "recommendation_type")
    private String recommendationType;
    private String title;
    @Column(columnDefinition = "text")
    private String body;
    @Column(columnDefinition = "text")
    private String evidence;
    private int priority;
    private String status;
    @Column(name = "valid_until")
    private Instant validUntil;
    @Column(name = "created_at")
    private Instant createdAt;

    protected Recommendation() {
    }

    public Recommendation(UUID userId, String recommendationType, String title, String body, String evidence, int priority) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.recommendationType = recommendationType;
        this.title = title;
        this.body = body;
        this.evidence = evidence;
        this.priority = priority;
        this.status = "ACTIVE";
        this.validUntil = Instant.now().plusSeconds(7 * 24 * 3600L);
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getRecommendationType() { return recommendationType; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getEvidence() { return evidence; }
    public int getPriority() { return priority; }
}

