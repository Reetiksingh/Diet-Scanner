package com.nutrilens.nutrition.application;

import com.nutrilens.common.domain.NutritionTotals;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NutritionDecisionEngine {
    public String classify(NutritionTotals totals) {
        List<String> reasons = new ArrayList<>();
        if (totals.sugarG().doubleValue() > 12) {
            reasons.add("high sugar");
        }
        if (totals.sodiumMg().doubleValue() > 600) {
            reasons.add("high sodium");
        }
        if (totals.fiberG().doubleValue() < 3 && totals.calories().doubleValue() > 350) {
            reasons.add("low fiber density");
        }
        if (reasons.size() >= 2) {
            return "AVOID: " + String.join(", ", reasons);
        }
        if (!reasons.isEmpty()) {
            return "MODERATE: " + String.join(", ", reasons);
        }
        return "SAFE: nutrition profile is within general platform thresholds";
    }
}

