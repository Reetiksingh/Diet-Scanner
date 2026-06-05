package com.nutrilens.analytics;

import com.nutrilens.analytics.application.AnalyticsScoringEngine;
import com.nutrilens.common.domain.NutritionTotals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AnalyticsScoringEngineTests {
    @Test
    void producesBoundedScores() {
        var totals = new NutritionTotals(BigDecimal.valueOf(2100), BigDecimal.valueOf(120), BigDecimal.valueOf(240),
                BigDecimal.valueOf(70), BigDecimal.valueOf(32), BigDecimal.valueOf(38), BigDecimal.valueOf(1500), 2400);
        var scores = new AnalyticsScoringEngine().score(totals, false, false);
        Assertions.assertTrue(scores.nutritionScore() >= 0 && scores.nutritionScore() <= 100);
        Assertions.assertTrue(scores.dietQualityScore() >= 0 && scores.dietQualityScore() <= 100);
    }
}

