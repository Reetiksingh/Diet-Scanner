package com.nutrilens.user.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class UserDtos {
    private UserDtos() {
    }

    public record ProfileResponse(
            UUID userId,
            String email,
            String displayName,
            String avatarUrl,
            String countryCode,
            String timezone,
            Integer age,
            BigDecimal heightCm,
            BigDecimal weightKg,
            String gender,
            String activityLevel,
            String dietaryPreference,
            GoalResponse activeGoal
    ) {
    }

    public record UpdateProfileRequest(
            @NotBlank String displayName,
            String avatarUrl,
            @NotBlank String countryCode,
            @NotBlank String timezone,
            @Min(13) @Max(120) Integer age,
            @Positive BigDecimal heightCm,
            @Positive BigDecimal weightKg,
            String gender,
            @NotBlank String activityLevel,
            @NotBlank String dietaryPreference
    ) {
    }

    public record GoalRequest(
            @NotBlank String goalType,
            @NotNull @Positive Integer targetCalories,
            @NotNull @Positive BigDecimal proteinTargetG,
            @NotNull @Positive BigDecimal carbsTargetG,
            @NotNull @Positive BigDecimal fatsTargetG,
            @NotNull @Positive Integer waterTargetMl
    ) {
    }

    public record GoalResponse(
            UUID id,
            String goalType,
            Integer targetCalories,
            BigDecimal proteinTargetG,
            BigDecimal carbsTargetG,
            BigDecimal fatsTargetG,
            Integer waterTargetMl
    ) {
    }

    public record BodyMeasurementRequest(
            @NotNull LocalDate measuredOn,
            @NotNull @Positive BigDecimal weightKg,
            BigDecimal bodyFatPercent
    ) {
    }
}

