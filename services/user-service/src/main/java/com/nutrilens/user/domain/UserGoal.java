package com.nutrilens.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_goals", schema = "users")
public class UserGoal {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "goal_type", nullable = false)
    private String goalType;
    @Column(name = "target_weight_kg")
    private BigDecimal targetWeightKg;
    @Column(name = "target_calories")
    private Integer targetCalories;
    @Column(name = "protein_target_g")
    private BigDecimal proteinTargetG;
    @Column(name = "carbs_target_g")
    private BigDecimal carbsTargetG;
    @Column(name = "fats_target_g")
    private BigDecimal fatsTargetG;
    @Column(name = "water_target_ml")
    private Integer waterTargetMl;
    private boolean active;
    @Column(name = "created_at")
    private Instant createdAt;

    protected UserGoal() {
    }

    public UserGoal(UUID userId, String goalType, Integer targetCalories, BigDecimal proteinTargetG, BigDecimal carbsTargetG, BigDecimal fatsTargetG, Integer waterTargetMl) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.goalType = goalType;
        this.targetCalories = targetCalories;
        this.proteinTargetG = proteinTargetG;
        this.carbsTargetG = carbsTargetG;
        this.fatsTargetG = fatsTargetG;
        this.waterTargetMl = waterTargetMl;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getGoalType() {
        return goalType;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public BigDecimal getProteinTargetG() {
        return proteinTargetG;
    }

    public BigDecimal getCarbsTargetG() {
        return carbsTargetG;
    }

    public BigDecimal getFatsTargetG() {
        return fatsTargetG;
    }

    public Integer getWaterTargetMl() {
        return waterTargetMl;
    }
}

