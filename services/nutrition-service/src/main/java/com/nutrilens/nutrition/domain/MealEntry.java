package com.nutrilens.nutrition.domain;

import com.nutrilens.common.domain.NutritionTotals;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "meal_entries", schema = "nutrition")
public class MealEntry {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal calories;
    @Column(name = "protein_g")
    private BigDecimal proteinG;
    @Column(name = "carbs_g")
    private BigDecimal carbsG;
    @Column(name = "fats_g")
    private BigDecimal fatsG;
    @Column(name = "fiber_g")
    private BigDecimal fiberG;
    @Column(name = "sugar_g")
    private BigDecimal sugarG;
    @Column(name = "sodium_mg")
    private BigDecimal sodiumMg;

    protected MealEntry() {
    }

    public MealEntry(Food food, BigDecimal quantity, String unit, NutritionTotals totals) {
        this.id = UUID.randomUUID();
        this.food = food;
        this.quantity = quantity;
        this.unit = unit;
        this.calories = totals.calories();
        this.proteinG = totals.proteinG();
        this.carbsG = totals.carbsG();
        this.fatsG = totals.fatsG();
        this.fiberG = totals.fiberG();
        this.sugarG = totals.sugarG();
        this.sodiumMg = totals.sodiumMg();
    }

    void attachMeal(Meal meal) {
        this.meal = meal;
    }

    public NutritionTotals totals() {
        return new NutritionTotals(calories, proteinG, carbsG, fatsG, fiberG, sugarG, sodiumMg, 0);
    }
}

