package com.nutrilens.common.events;

import com.nutrilens.common.domain.NutritionTotals;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public final class Events {
    private Events() {
    }

    public record UserRegisteredEvent(UUID userId, String email, String displayName, String countryCode) {
    }

    public record UserLoggedInEvent(UUID userId, String provider, Instant loggedInAt) {
    }

    public record SessionRevokedEvent(UUID userId, UUID familyId, String reason) {
    }

    public record UserProfileUpdatedEvent(UUID userId, String countryCode, String activityLevel, String dietaryPreference) {
    }

    public record UserGoalUpdatedEvent(UUID userId, String goalType, int calories, double proteinG, double carbsG, double fatsG, int waterMl) {
    }

    public record MealLoggedEvent(UUID userId, UUID mealId, String mealType, Instant consumedAt, NutritionTotals totals) {
    }

    public record MealUpdatedEvent(UUID userId, UUID mealId, Instant consumedAt, NutritionTotals totals) {
    }

    public record MealDeletedEvent(UUID userId, UUID mealId, LocalDate recordDate) {
    }

    public record FoodCreatedEvent(UUID foodId, UUID createdByUserId, String name, String barcode) {
    }

    public record NutritionGoalReachedEvent(UUID userId, LocalDate date, String goalType, int score) {
    }

    public record DailyAnalyticsComputedEvent(UUID userId, LocalDate date, int nutritionScore, int consistencyScore, int hydrationScore, int streakDays) {
    }

    public record WeeklyReportGeneratedEvent(UUID userId, LocalDate weekStart, LocalDate weekEnd, int averageNutritionScore, int averageConsistencyScore) {
    }

    public record NutritionInsightGeneratedEvent(UUID userId, String insightType, String message, Map<String, Object> evidence) {
    }

    public record StreakUpdatedEvent(UUID userId, int streakDays, LocalDate asOfDate) {
    }

    public record AchievementUnlockedEvent(UUID userId, UUID achievementId, String code, String name, Instant unlockedAt) {
    }

    public record ChallengeCompletedEvent(UUID userId, UUID challengeId, String challengeCode, double score) {
    }

    public record LeaderboardUpdatedEvent(String leaderboardKey, UUID userId, double score, long rank) {
    }

    public record RecommendationGeneratedEvent(UUID userId, UUID recommendationId, String recommendationType, String title) {
    }

    public record NotificationCreatedEvent(UUID userId, UUID notificationId, String type, String title) {
    }
}

