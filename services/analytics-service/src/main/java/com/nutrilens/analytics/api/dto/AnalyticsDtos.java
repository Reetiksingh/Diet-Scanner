package com.nutrilens.analytics.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class AnalyticsDtos {
    private AnalyticsDtos() {
    }

    public record DailyAnalyticsResponse(LocalDate date, BigDecimal calories, BigDecimal proteinG, BigDecimal carbsG,
                                         BigDecimal fatsG, BigDecimal fiberG, BigDecimal sugarG, int waterMl,
                                         int nutritionScore, int macroBalanceScore, int consistencyScore,
                                         int hydrationScore, int dietQualityScore, boolean skippedBreakfast) {
    }

    public record SummaryResponse(LocalDate date, int nutritionScore, int consistencyScore, int hydrationScore,
                                  int dietQualityScore, Map<String, Integer> macroBalance, int streakDays,
                                  String topInsight) {
    }

    public record InsightResponse(String type, String title, String message, String severity, String evidence) {
    }

    public record TrendResponse(List<DailyAnalyticsResponse> points) {
    }
}

