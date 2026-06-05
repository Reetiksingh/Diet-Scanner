package com.nutrilens.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record NutritionTotals(
        BigDecimal calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatsG,
        BigDecimal fiberG,
        BigDecimal sugarG,
        BigDecimal sodiumMg,
        Integer waterMl
) {
    public NutritionTotals {
        calories = value(calories);
        proteinG = value(proteinG);
        carbsG = value(carbsG);
        fatsG = value(fatsG);
        fiberG = value(fiberG);
        sugarG = value(sugarG);
        sodiumMg = value(sodiumMg);
        waterMl = waterMl == null ? 0 : Math.max(0, waterMl);
    }

    public static NutritionTotals zero() {
        return new NutritionTotals(null, null, null, null, null, null, null, 0);
    }

    public NutritionTotals add(NutritionTotals other) {
        return new NutritionTotals(
                calories.add(other.calories),
                proteinG.add(other.proteinG),
                carbsG.add(other.carbsG),
                fatsG.add(other.fatsG),
                fiberG.add(other.fiberG),
                sugarG.add(other.sugarG),
                sodiumMg.add(other.sodiumMg),
                waterMl + other.waterMl
        );
    }

    public NutritionTotals multiply(BigDecimal factor) {
        return new NutritionTotals(
                calories.multiply(factor),
                proteinG.multiply(factor),
                carbsG.multiply(factor),
                fatsG.multiply(factor),
                fiberG.multiply(factor),
                sugarG.multiply(factor),
                sodiumMg.multiply(factor),
                waterMl
        );
    }

    public int clampScore(BigDecimal score) {
        return score.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private static BigDecimal value(BigDecimal input) {
        if (input == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return input.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}

