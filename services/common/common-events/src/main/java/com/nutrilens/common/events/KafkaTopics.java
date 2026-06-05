package com.nutrilens.common.events;

import java.util.List;

public final class KafkaTopics {
    public static final String USER_REGISTERED = "auth.user-registered.v1";
    public static final String USER_LOGGED_IN = "auth.user-logged-in.v1";
    public static final String SESSION_REVOKED = "auth.session-revoked.v1";
    public static final String USER_PROFILE_UPDATED = "users.profile-updated.v1";
    public static final String USER_GOAL_UPDATED = "users.goal-updated.v1";
    public static final String MEAL_LOGGED = "nutrition.meal-logged.v1";
    public static final String MEAL_UPDATED = "nutrition.meal-updated.v1";
    public static final String MEAL_DELETED = "nutrition.meal-deleted.v1";
    public static final String FOOD_CREATED = "nutrition.food-created.v1";
    public static final String NUTRITION_GOAL_REACHED = "nutrition.goal-reached.v1";
    public static final String DAILY_COMPUTED = "analytics.daily-computed.v1";
    public static final String WEEKLY_REPORT_GENERATED = "analytics.weekly-report-generated.v1";
    public static final String INSIGHT_GENERATED = "analytics.insight-generated.v1";
    public static final String STREAK_UPDATED = "gamification.streak-updated.v1";
    public static final String ACHIEVEMENT_UNLOCKED = "gamification.achievement-unlocked.v1";
    public static final String CHALLENGE_COMPLETED = "gamification.challenge-completed.v1";
    public static final String LEADERBOARD_UPDATED = "leaderboard.updated.v1";
    public static final String RECOMMENDATION_GENERATED = "recommendations.recommendation-generated.v1";
    public static final String NOTIFICATION_CREATED = "notifications.notification-created.v1";

    private KafkaTopics() {
    }

    public static List<String> all() {
        return List.of(
                USER_REGISTERED,
                USER_LOGGED_IN,
                SESSION_REVOKED,
                USER_PROFILE_UPDATED,
                USER_GOAL_UPDATED,
                MEAL_LOGGED,
                MEAL_UPDATED,
                MEAL_DELETED,
                FOOD_CREATED,
                NUTRITION_GOAL_REACHED,
                DAILY_COMPUTED,
                WEEKLY_REPORT_GENERATED,
                INSIGHT_GENERATED,
                STREAK_UPDATED,
                ACHIEVEMENT_UNLOCKED,
                CHALLENGE_COMPLETED,
                LEADERBOARD_UPDATED,
                RECOMMENDATION_GENERATED,
                NOTIFICATION_CREATED
        );
    }
}

