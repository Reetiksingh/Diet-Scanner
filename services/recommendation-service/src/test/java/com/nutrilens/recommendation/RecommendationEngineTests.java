package com.nutrilens.recommendation;

import com.nutrilens.recommendation.application.RecommendationEngine;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RecommendationEngineTests {
    @Test
    void generatesEvidenceBasedRecommendation() {
        var recommendations = new RecommendationEngine().fromDailyAnalytics(UUID.randomUUID(), LocalDate.now(), 60, 70, 90);
        Assertions.assertFalse(recommendations.isEmpty());
    }
}

