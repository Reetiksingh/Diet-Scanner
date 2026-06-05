package com.nutrilens.analytics.application;

import com.nutrilens.common.domain.NutritionTotals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsScoringEngine {
    public ScoreBundle score(NutritionTotals totals, boolean skippedBreakfast, boolean lateNightEating) {
        int protein = ratioScore(totals.proteinG(), BigDecimal.valueOf(110));
        int fiber = ratioScore(totals.fiberG(), BigDecimal.valueOf(30));
        int sugar = inverseScore(totals.sugarG(), BigDecimal.valueOf(55));
        int macro = macroScore(totals);
        int hydration = ratioScore(BigDecimal.valueOf(totals.waterMl()), BigDecimal.valueOf(2500));
        int dietQuality = clamp((protein * 0.30) + (fiber * 0.20) + (sugar * 0.20) + (macro * 0.20) + (hydration * 0.10));
        int consistency = clamp(100 - (skippedBreakfast ? 18 : 0) - (lateNightEating ? 14 : 0));
        int nutrition = clamp((dietQuality * 0.55) + (macro * 0.25) + (consistency * 0.20));
        return new ScoreBundle(nutrition, macro, consistency, hydration, dietQuality);
    }

    private int ratioScore(BigDecimal actual, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return clamp(actual.divide(target, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
    }

    private int inverseScore(BigDecimal actual, BigDecimal limit) {
        if (actual.compareTo(limit) <= 0) {
            return 100;
        }
        return clamp(100 - actual.subtract(limit).multiply(BigDecimal.valueOf(2)).doubleValue());
    }

    private int macroScore(NutritionTotals totals) {
        BigDecimal calories = totals.calories().max(BigDecimal.ONE);
        double proteinPct = totals.proteinG().multiply(BigDecimal.valueOf(4)).divide(calories, 4, RoundingMode.HALF_UP).doubleValue();
        double carbsPct = totals.carbsG().multiply(BigDecimal.valueOf(4)).divide(calories, 4, RoundingMode.HALF_UP).doubleValue();
        double fatsPct = totals.fatsG().multiply(BigDecimal.valueOf(9)).divide(calories, 4, RoundingMode.HALF_UP).doubleValue();
        double deviation = Math.abs(proteinPct - 0.30) + Math.abs(carbsPct - 0.45) + Math.abs(fatsPct - 0.25);
        return clamp(100 - deviation * 120);
    }

    private int clamp(double score) {
        return Math.max(0, Math.min(100, (int) Math.round(score)));
    }

    public record ScoreBundle(int nutritionScore, int macroBalanceScore, int consistencyScore, int hydrationScore, int dietQualityScore) {
    }
}

