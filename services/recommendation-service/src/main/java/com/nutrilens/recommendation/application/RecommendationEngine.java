package com.nutrilens.recommendation.application;

import com.nutrilens.recommendation.domain.Recommendation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RecommendationEngine {
    public List<Recommendation> fromDailyAnalytics(UUID userId, LocalDate date, int nutritionScore, int consistencyScore, int hydrationScore) {
        List<Recommendation> recommendations = new ArrayList<>();
        if (nutritionScore < 75) {
            recommendations.add(new Recommendation(userId, "NUTRITION_SCORE",
                    "Raise nutrition quality score",
                    "Increase protein and fiber density in the next two meals to improve the deterministic nutrition quality score.",
                    "{\"date\":\"" + date + "\",\"nutritionScore\":" + nutritionScore + "}", 90));
        }
        if (consistencyScore < 80) {
            recommendations.add(new Recommendation(userId, "CONSISTENCY",
                    "Stabilize meal timing",
                    "The consistency score suggests meal timing or logging gaps. Keep breakfast and dinner within a repeatable window for the next seven days.",
                    "{\"date\":\"" + date + "\",\"consistencyScore\":" + consistencyScore + "}", 80));
        }
        if (hydrationScore < 85) {
            recommendations.add(new Recommendation(userId, "HYDRATION",
                    "Close hydration gap",
                    "Hydration score is below target. Add structured water check-ins before lunch and dinner.",
                    "{\"date\":\"" + date + "\",\"hydrationScore\":" + hydrationScore + "}", 70));
        }
        if (recommendations.isEmpty()) {
            recommendations.add(new Recommendation(userId, "MAINTAIN",
                    "Maintain current nutrition rhythm",
                    "Scores are strong today. Keep the current macro distribution and meal timing pattern.",
                    "{\"date\":\"" + date + "\",\"nutritionScore\":" + nutritionScore + "}", 50));
        }
        return recommendations;
    }
}

