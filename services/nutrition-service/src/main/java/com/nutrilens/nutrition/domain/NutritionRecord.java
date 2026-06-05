package com.nutrilens.nutrition.domain;

import com.nutrilens.common.domain.NutritionTotals;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "nutrition_records", schema = "nutrition",
        uniqueConstraints = @UniqueConstraint(name = "uk_nutrition_record_user_date", columnNames = {"user_id", "record_date"}))
public class NutritionRecord {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "record_date")
    private LocalDate recordDate;
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
    @Column(name = "sodium_mg")
    private BigDecimal sodiumMg = BigDecimal.ZERO;

    protected NutritionRecord() {
    }

    public NutritionRecord(UUID userId, LocalDate recordDate) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.recordDate = recordDate;
    }

    public void add(NutritionTotals totals) {
        this.calories = calories.add(totals.calories());
        this.proteinG = proteinG.add(totals.proteinG());
        this.carbsG = carbsG.add(totals.carbsG());
        this.fatsG = fatsG.add(totals.fatsG());
        this.fiberG = fiberG.add(totals.fiberG());
        this.sugarG = sugarG.add(totals.sugarG());
        this.sodiumMg = sodiumMg.add(totals.sodiumMg());
        this.waterMl = waterMl + totals.waterMl();
    }
}

