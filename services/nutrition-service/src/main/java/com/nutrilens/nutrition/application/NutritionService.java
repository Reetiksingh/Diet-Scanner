package com.nutrilens.nutrition.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrilens.common.domain.NutritionTotals;
import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.security.SecuritySupport;
import com.nutrilens.nutrition.api.dto.NutritionDtos.BarcodeScanRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.FoodRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.FoodResponse;
import com.nutrilens.nutrition.api.dto.NutritionDtos.LabelScanRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.MealRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.MealResponse;
import com.nutrilens.nutrition.api.dto.NutritionDtos.NutritionPayload;
import com.nutrilens.nutrition.api.dto.NutritionDtos.ScanResponse;
import com.nutrilens.nutrition.domain.Food;
import com.nutrilens.nutrition.domain.FoodNutrition;
import com.nutrilens.nutrition.domain.Meal;
import com.nutrilens.nutrition.domain.MealEntry;
import com.nutrilens.nutrition.domain.NutritionRecord;
import com.nutrilens.nutrition.domain.ScanJob;
import com.nutrilens.nutrition.infrastructure.FoodRepository;
import com.nutrilens.nutrition.infrastructure.MealRepository;
import com.nutrilens.nutrition.infrastructure.NutritionRecordRepository;
import com.nutrilens.nutrition.infrastructure.ScanJobRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NutritionService {
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final NutritionRecordRepository nutritionRecordRepository;
    private final ScanJobRepository scanJobRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NutritionTextParser nutritionTextParser;
    private final NutritionDecisionEngine decisionEngine;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public NutritionService(FoodRepository foodRepository,
                            MealRepository mealRepository,
                            NutritionRecordRepository nutritionRecordRepository,
                            ScanJobRepository scanJobRepository,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            NutritionTextParser nutritionTextParser,
                            NutritionDecisionEngine decisionEngine,
                            StringRedisTemplate redisTemplate,
                            ObjectMapper objectMapper) {
        this.foodRepository = foodRepository;
        this.mealRepository = mealRepository;
        this.nutritionRecordRepository = nutritionRecordRepository;
        this.scanJobRepository = scanJobRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.nutritionTextParser = nutritionTextParser;
        this.decisionEngine = decisionEngine;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> search(String query, String barcode) {
        if (barcode != null && !barcode.isBlank()) {
            return foodRepository.findByBarcode(barcode).map(food -> List.of(toFoodResponse(food))).orElse(List.of());
        }
        String safeQuery = query == null ? "" : query;
        return foodRepository.findTop25ByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(safeQuery, safeQuery).stream()
                .map(this::toFoodResponse)
                .toList();
    }

    @Transactional
    public FoodResponse createFood(FoodRequest request) {
        UUID userId = SecuritySupport.currentUserId();
        Food food = new Food(request.barcode(), request.name(), request.brand(), "CUSTOM", request.servingSize(), request.servingUnit(), userId);
        food.attachNutrition(toFoodNutrition(request.nutrition()));
        Food saved = foodRepository.save(food);
        kafkaTemplate.send(KafkaTopics.FOOD_CREATED, saved.getId().toString(), EventEnvelope.of(
                "FoodCreatedEvent",
                "nutrition-service",
                UUID.randomUUID().toString(),
                new Events.FoodCreatedEvent(saved.getId(), userId, saved.getName(), saved.getBarcode())
        ));
        return toFoodResponse(saved);
    }

    @Transactional(readOnly = true)
    public FoodResponse getFood(UUID id) {
        return foodRepository.findById(id).map(this::toFoodResponse).orElseThrow(() -> new IllegalArgumentException("Food not found"));
    }

    @Transactional
    public MealResponse logMeal(MealRequest request) {
        UUID userId = SecuritySupport.currentUserId();
        String timezone = request.timezone() == null || request.timezone().isBlank() ? "Asia/Kolkata" : request.timezone();
        Meal meal = new Meal(userId, request.mealType(), request.consumedAt(), timezone, request.notes());
        NutritionTotals totals = NutritionTotals.zero();
        for (var entryRequest : request.entries()) {
            Food food = foodRepository.findById(entryRequest.foodId()).orElseThrow(() -> new IllegalArgumentException("Food not found: " + entryRequest.foodId()));
            BigDecimal factor = entryRequest.quantity().divide(food.getServingSize() == null || BigDecimal.ZERO.compareTo(food.getServingSize()) == 0
                    ? BigDecimal.valueOf(100)
                    : food.getServingSize(), 4, java.math.RoundingMode.HALF_UP);
            NutritionTotals entryTotals = food.getNutrition().totals().multiply(factor);
            totals = totals.add(entryTotals);
            meal.addEntry(new MealEntry(food, entryRequest.quantity(), entryRequest.unit(), entryTotals));
        }
        Meal saved = mealRepository.save(meal);
        LocalDate recordDate = request.consumedAt().atZone(ZoneId.of(timezone)).toLocalDate();
        NutritionRecord record = nutritionRecordRepository.findByUserIdAndRecordDate(userId, recordDate)
                .orElseGet(() -> new NutritionRecord(userId, recordDate));
        record.add(totals);
        nutritionRecordRepository.save(record);
        UUID eventId = UUID.randomUUID();
        kafkaTemplate.send(KafkaTopics.MEAL_LOGGED, userId.toString(), new EventEnvelope<>(
                eventId,
                "MealLoggedEvent",
                1,
                java.time.Instant.now(),
                UUID.randomUUID().toString(),
                "nutrition-service",
                new Events.MealLoggedEvent(userId, saved.getId(), saved.getMealType(), saved.getConsumedAt(), totals)
        ));
        redisTemplate.delete("analytics:summary:" + userId);
        redisTemplate.delete("recommendations:current:" + userId);
        return new MealResponse(saved.getId(), saved.getMealType(), saved.getConsumedAt(), toPayload(totals), eventId);
    }

    @Transactional
    public ScanResponse scanBarcode(BarcodeScanRequest request) {
        Food food = foodRepository.findByBarcode(request.barcode()).orElseThrow(() -> new IllegalArgumentException("Barcode not found"));
        NutritionTotals totals = food.getNutrition().totals();
        return new ScanResponse(UUID.randomUUID(), "COMPLETED", toPayload(totals), decisionEngine.classify(totals));
    }

    @Transactional
    public ScanResponse scanLabel(LabelScanRequest request) {
        UUID userId = SecuritySupport.currentUserId();
        NutritionTotals totals = nutritionTextParser.parse(request.rawText());
        String decision = decisionEngine.classify(totals);
        try {
            String result = objectMapper.writeValueAsString(toPayload(totals));
            ScanJob job = scanJobRepository.save(new ScanJob(userId, "LABEL", request.rawText(), result));
            return new ScanResponse(job.getId(), job.getStatus(), toPayload(totals), decision);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to store scan result", exception);
        }
    }

    private FoodNutrition toFoodNutrition(NutritionPayload payload) {
        return new FoodNutrition(payload.calories(), payload.proteinG(), payload.carbsG(), payload.fatsG(), payload.fiberG(), payload.sugarG(), payload.sodiumMg());
    }

    private NutritionPayload toPayload(NutritionTotals totals) {
        return new NutritionPayload(totals.calories(), totals.proteinG(), totals.carbsG(), totals.fatsG(), totals.fiberG(), totals.sugarG(), totals.sodiumMg());
    }

    private FoodResponse toFoodResponse(Food food) {
        NutritionTotals totals = food.getNutrition() == null ? NutritionTotals.zero() : food.getNutrition().totals();
        return new FoodResponse(food.getId(), food.getBarcode(), food.getName(), food.getBrand(), food.getSource(),
                food.getServingSize(), food.getServingUnit(), toPayload(totals));
    }
}

