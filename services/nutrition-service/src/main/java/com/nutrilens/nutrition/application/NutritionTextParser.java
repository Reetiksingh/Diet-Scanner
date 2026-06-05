package com.nutrilens.nutrition.application;

import com.nutrilens.common.domain.NutritionTotals;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class NutritionTextParser {
    public NutritionTotals parse(String rawText) {
        String normalized = rawText == null ? "" : rawText.replaceAll("\\s+", " ").toLowerCase();
        return new NutritionTotals(
                value(normalized, List.of("calories", "calorie", "energy", "kcal"), false),
                value(normalized, List.of("protein", "protien", "prot"), false),
                value(normalized, List.of("carbohydrate", "carbs", "carb"), false),
                value(normalized, List.of("total fat", "fat", "fats"), false),
                value(normalized, List.of("fiber", "fibre"), false),
                value(normalized, List.of("total sugar", "sugars", "sugar"), false),
                value(normalized, List.of("sodium", "salt"), true),
                0
        );
    }

    private BigDecimal value(String text, List<String> aliases, boolean sodiumMg) {
        for (String alias : aliases) {
            Pattern pattern = Pattern.compile(Pattern.quote(alias) + "\\s*(?:\\([^)]*\\))?\\s*[:\\-]?\\s*(\\d+(?:[.,]\\d+)?)\\s*(kcal|cal|mg|g)?", Pattern.CASE_INSENSITIVE);
            var matcher = pattern.matcher(text);
            if (matcher.find()) {
                BigDecimal parsed = new BigDecimal(matcher.group(1).replace(",", "."));
                String unit = matcher.group(2) == null ? "" : matcher.group(2).toLowerCase();
                if (sodiumMg && "g".equals(unit)) {
                    return parsed.multiply(BigDecimal.valueOf(1000));
                }
                if (!sodiumMg && "mg".equals(unit)) {
                    return parsed.divide(BigDecimal.valueOf(1000));
                }
                return parsed;
            }
        }
        return BigDecimal.ZERO;
    }
}

