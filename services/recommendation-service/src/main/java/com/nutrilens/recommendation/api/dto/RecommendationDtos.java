package com.nutrilens.recommendation.api.dto;

import java.util.List;
import java.util.UUID;

public final class RecommendationDtos {
    private RecommendationDtos() {
    }

    public record RecommendationResponse(UUID id, String type, String title, String body, String evidence, int priority) {
    }

    public record RecommendationBundle(List<RecommendationResponse> recommendations) {
    }

    public record ReportResponse(String period, String summary, List<RecommendationResponse> recommendations) {
    }
}

