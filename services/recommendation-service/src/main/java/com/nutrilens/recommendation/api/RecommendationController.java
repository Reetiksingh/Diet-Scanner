package com.nutrilens.recommendation.api;

import com.nutrilens.recommendation.api.dto.RecommendationDtos.RecommendationBundle;
import com.nutrilens.recommendation.api.dto.RecommendationDtos.ReportResponse;
import com.nutrilens.recommendation.application.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/current")
    public RecommendationBundle current() {
        return recommendationService.current();
    }

    @GetMapping("/weekly-summary")
    public ReportResponse weekly() {
        return recommendationService.weekly();
    }

    @GetMapping("/monthly-summary")
    public ReportResponse monthly() {
        return recommendationService.monthly();
    }

    @PostMapping("/recalculate")
    public RecommendationBundle recalculate() {
        return recommendationService.current();
    }
}

