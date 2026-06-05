package com.nutrilens.analytics.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "weekly_analytics", schema = "analytics")
public class WeeklyAnalytics {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "week_start")
    private LocalDate weekStart;
    @Column(name = "week_end")
    private LocalDate weekEnd;
    @Column(name = "avg_nutrition_score")
    private int avgNutritionScore;
    @Column(name = "avg_consistency_score")
    private int avgConsistencyScore;
    @Column(name = "avg_hydration_score")
    private int avgHydrationScore;
    @Column(name = "streak_days")
    private int streakDays;
    @Column(name = "calorie_spike_count")
    private int calorieSpikeCount;
    @Column(name = "late_night_eating_count")
    private int lateNightEatingCount;
    @Column(name = "generated_at")
    private Instant generatedAt;

    protected WeeklyAnalytics() {
    }

    public WeeklyAnalytics(UUID userId, LocalDate weekStart, LocalDate weekEnd, int avgNutritionScore, int avgConsistencyScore, int avgHydrationScore, int streakDays) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.avgNutritionScore = avgNutritionScore;
        this.avgConsistencyScore = avgConsistencyScore;
        this.avgHydrationScore = avgHydrationScore;
        this.streakDays = streakDays;
        this.generatedAt = Instant.now();
    }
}

