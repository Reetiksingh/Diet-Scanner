package com.nutrilens.analytics.api;

import com.nutrilens.analytics.api.dto.AnalyticsDtos.DailyAnalyticsResponse;
import com.nutrilens.analytics.api.dto.AnalyticsDtos.InsightResponse;
import com.nutrilens.analytics.api.dto.AnalyticsDtos.SummaryResponse;
import com.nutrilens.analytics.application.AnalyticsService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public SummaryResponse summary() {
        return analyticsService.summary();
    }

    @GetMapping("/daily")
    public List<DailyAnalyticsResponse> daily(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return analyticsService.daily(from, to);
    }

    @GetMapping("/insights")
    public List<InsightResponse> insights() {
        return analyticsService.insights();
    }
}

