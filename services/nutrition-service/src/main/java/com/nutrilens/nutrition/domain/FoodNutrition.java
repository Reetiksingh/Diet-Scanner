package com.nutrilens.nutrition.domain;

import com.nutrilens.common.domain.NutritionTotals;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "food_nutrition", schema = "nutrition")
public class FoodNutrition {
    @Id
    @Column(name = "food_id")
    private UUID foodId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "food_id")
    private Food food;
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
    @Column(name = "potassium_mg")
    private BigDecimal potassiumMg;
    @Column(name = "saturated_fat_g")
    private BigDecimal saturatedFatG;

    protected FoodNutrition() {
    }

    public FoodNutrition(BigDecimal calories, BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatsG, BigDecimal fiberG, BigDecimal sugarG, BigDecimal sodiumMg) {
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatsG = fatsG;
        this.fiberG = fiberG;
        this.sugarG = sugarG;
        this.sodiumMg = sodiumMg;
    }

    void attachFood(Food food) {
        this.food = food;
        this.foodId = food.getId();
    }

    public NutritionTotals totals() {
        return new NutritionTotals(calories, proteinG, carbsG, fatsG, fiberG, sugarG, sodiumMg, 0);
    }
}

