package com.nutrilens.analytics.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_analytics", schema = "analytics",
        uniqueConstraints = @UniqueConstraint(name = "uk_daily_analytics_user_date", columnNames = {"user_id", "analytics_date"}))
public class DailyAnalytics {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "analytics_date", nullable = false)
    private LocalDate analyticsDate;
    private BigDecimal calories = BigDecimal.ZERO;
    @Column(name = "protein_g")
    private BigDecimal proteinG = BigDecimal.ZERO;
    @Column(name = "carbs_g")
    private BigDecimal carbsG = BigDecimal.ZERO;
    @Column(name = "fats_g")
    private BigDecimal fatsG = BigDecimal.ZERO;
    @Column(name = "fiber_g")
    private BigDecimal fiberG = BigDecimal.ZERO;
    @Column(name = "sugar_g")
    private BigDecimal sugarG = BigDecimal.ZERO;
    @Column(name = "water_ml")
    private Integer waterMl = 0;
    @Column(name = "nutrition_score")
    private Integer nutritionScore = 0;
    @Column(name = "macro_balance_score")
    private Integer macroBalanceScore = 0;
    @Column(name = "consistency_score")
    private Integer consistencyScore = 0;
    @Column(name = "hydration_score")
    private Integer hydrationScore = 0;
    @Column(name = "diet_quality_score")
    private Integer dietQualityScore = 0;
    @Column(name = "late_night_calorie_ratio")
    private BigDecimal lateNightCalorieRatio = BigDecimal.ZERO;
    @Column(name = "skipped_breakfast")
    private boolean skippedBreakfast;
    @Column(name = "meal_timing_variance_minutes")
    private Integer mealTimingVarianceMinutes = 0;
    @Column(name = "computed_at")
    private Instant computedAt;

    protected DailyAnalytics() {
    }

    public DailyAnalytics(UUID userId, LocalDate analyticsDate) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.analyticsDate = analyticsDate;
    }

    public void apply(BigDecimal calories, BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatsG, BigDecimal fiberG, BigDecimal sugarG,
                      int waterMl, int nutritionScore, int macroBalanceScore, int consistencyScore, int hydrationScore,
                      int dietQualityScore, BigDecimal lateNightCalorieRatio, boolean skippedBreakfast, int mealTimingVarianceMinutes) {
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatsG = fatsG;
        this.fiberG = fiberG;
        this.sugarG = sugarG;
        this.waterMl = waterMl;
        this.nutritionScore = nutritionScore;
        this.macroBalanceScore = macroBalanceScore;
        this.consistencyScore = consistencyScore;
        this.hydrationScore = hydrationScore;
        this.dietQualityScore = dietQualityScore;
        this.lateNightCalorieRatio = lateNightCalorieRatio;
        this.skippedBreakfast = skippedBreakfast;
        this.mealTimingVarianceMinutes = mealTimingVarianceMinutes;
        this.computedAt = Instant.now();
    }

    public UUID getUserId() { return userId; }
    public LocalDate getAnalyticsDate() { return analyticsDate; }
    public BigDecimal getCalories() { return calories; }
    public BigDecimal getProteinG() { return proteinG; }
    public BigDecimal getCarbsG() { return carbsG; }
    public BigDecimal getFatsG() { return fatsG; }
    public BigDecimal getFiberG() { return fiberG; }
    public BigDecimal getSugarG() { return sugarG; }
    public Integer getWaterMl() { return waterMl; }
    public Integer getNutritionScore() { return nutritionScore; }
    public Integer getMacroBalanceScore() { return macroBalanceScore; }
    public Integer getConsistencyScore() { return consistencyScore; }
    public Integer getHydrationScore() { return hydrationScore; }
    public Integer getDietQualityScore() { return dietQualityScore; }
    public boolean isSkippedBreakfast() { return skippedBreakfast; }
}

