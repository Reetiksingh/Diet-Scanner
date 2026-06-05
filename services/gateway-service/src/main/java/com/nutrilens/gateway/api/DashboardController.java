package com.nutrilens.gateway.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    @GetMapping("/summary")
    public DashboardSummary summary() {
        return new DashboardSummary(
                LocalDate.now(),
                86,
                79,
                92,
                Map.of("protein", 31, "carbs", 44, "fats", 25),
                18,
                "Protein intake increased 12% compared with the previous 14 days.",
                124
        );
    }

    public record DashboardSummary(
            LocalDate date,
            int nutritionScore,
            int consistencyScore,
            int hydrationScore,
            Map<String, Integer> macroBalance,
            int streakDays,
            String topInsight,
            int weeklyRank
    ) {
    }
}

