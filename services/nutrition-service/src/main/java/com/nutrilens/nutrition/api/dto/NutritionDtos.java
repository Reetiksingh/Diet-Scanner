package com.nutrilens.nutrition.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class NutritionDtos {
    private NutritionDtos() {
    }

    public record NutritionPayload(BigDecimal calories, BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatsG,
                                   BigDecimal fiberG, BigDecimal sugarG, BigDecimal sodiumMg) {
    }

    public record FoodRequest(String barcode, @NotBlank String name, String brand, BigDecimal servingSize,
                              String servingUnit, @Valid @NotNull NutritionPayload nutrition) {
    }

    public record FoodResponse(UUID id, String barcode, String name, String brand, String source,
                               BigDecimal servingSize, String servingUnit, NutritionPayload nutrition) {
    }

    public record MealEntryRequest(@NotNull UUID foodId, @NotNull @Positive BigDecimal quantity, @NotBlank String unit) {
    }

    public record MealRequest(@NotBlank String mealType, @NotNull Instant consumedAt, String timezone, String notes,
                              @NotEmpty List<@Valid MealEntryRequest> entries) {
    }

    public record MealResponse(UUID mealId, String mealType, Instant consumedAt, NutritionPayload totals, UUID eventId) {
    }

    public record BarcodeScanRequest(@NotBlank String barcode) {
    }

    public record LabelScanRequest(@NotBlank String rawText) {
    }

    public record ScanResponse(UUID scanId, String status, NutritionPayload nutrition, String decision) {
    }

    public record DailyRecordResponse(LocalDate date, NutritionPayload totals) {
    }
}

