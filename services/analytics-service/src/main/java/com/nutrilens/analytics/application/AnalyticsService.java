package com.nutrilens.analytics.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrilens.analytics.api.dto.AnalyticsDtos.DailyAnalyticsResponse;
import com.nutrilens.analytics.api.dto.AnalyticsDtos.InsightResponse;
import com.nutrilens.analytics.api.dto.AnalyticsDtos.SummaryResponse;
import com.nutrilens.analytics.domain.DailyAnalytics;
import com.nutrilens.analytics.domain.NutritionInsight;
import com.nutrilens.analytics.infrastructure.DailyAnalyticsRepository;
import com.nutrilens.analytics.infrastructure.NutritionInsightRepository;
import com.nutrilens.common.domain.NutritionTotals;
import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.security.SecuritySupport;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
    private final DailyAnalyticsRepository dailyAnalyticsRepository;
    private final NutritionInsightRepository nutritionInsightRepository;
    private final AnalyticsScoringEngine scoringEngine;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public AnalyticsService(DailyAnalyticsRepository dailyAnalyticsRepository,
                            NutritionInsightRepository nutritionInsightRepository,
                            AnalyticsScoringEngine scoringEngine,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            StringRedisTemplate redisTemplate,
                            ObjectMapper objectMapper) {
        this.dailyAnalyticsRepository = dailyAnalyticsRepository;
        this.nutritionInsightRepository = nutritionInsightRepository;
        this.scoringEngine = scoringEngine;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.MEAL_LOGGED, groupId = "analytics-service")
    @Transactional
    public void onMealLogged(EventEnvelope<?> envelope) {
        LinkedHashMap<?, ?> payload = (LinkedHashMap<?, ?>) envelope.payload();
        UUID userId = UUID.fromString(String.valueOf(payload.get("userId")));
        Instant consumedAt = Instant.parse(String.valueOf(payload.get("consumedAt")));
        String mealType = String.valueOf(payload.get("mealType"));
        @SuppressWarnings("unchecked")
        Map<String, Object> totalsMap = (Map<String, Object>) payload.get("totals");
        NutritionTotals totals = totalsFromMap(totalsMap);
        LocalDate date = consumedAt.atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        boolean skippedBreakfast = !"BREAKFAST".equalsIgnoreCase(mealType);
        boolean lateNight = consumedAt.atZone(ZoneId.of("Asia/Kolkata")).getHour() >= 21;
        AnalyticsScoringEngine.ScoreBundle scores = scoringEngine.score(totals, skippedBreakfast, lateNight);
        DailyAnalytics daily = dailyAnalyticsRepository.findByUserIdAndAnalyticsDate(userId, date).orElseGet(() -> new DailyAnalytics(userId, date));
        BigDecimal lateNightRatio = lateNight ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        daily.apply(totals.calories(), totals.proteinG(), totals.carbsG(), totals.fatsG(), totals.fiberG(), totals.sugarG(), totals.waterMl(),
                scores.nutritionScore(), scores.macroBalanceScore(), scores.consistencyScore(), scores.hydrationScore(), scores.dietQualityScore(),
                lateNightRatio, skippedBreakfast, lateNight ? 90 : 20);
        dailyAnalyticsRepository.save(daily);
        maybeGenerateInsight(userId, date, totals, lateNight, skippedBreakfast);
        cacheSummary(userId, daily);
        kafkaTemplate.send(KafkaTopics.DAILY_COMPUTED, userId.toString(), EventEnvelope.of(
                "DailyAnalyticsComputedEvent",
                "analytics-service",
                envelope.correlationId(),
                new Events.DailyAnalyticsComputedEvent(userId, date, scores.nutritionScore(), scores.consistencyScore(), scores.hydrationScore(), currentStreak(userId))
        ));
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary() {
        UUID userId = SecuritySupport.currentUserId();
        List<DailyAnalytics> days = dailyAnalyticsRepository.findByUserIdAndAnalyticsDateBetweenOrderByAnalyticsDate(userId, LocalDate.now().minusDays(30), LocalDate.now());
        DailyAnalytics latest = days.stream().max(Comparator.comparing(DailyAnalytics::getAnalyticsDate)).orElseGet(() -> emptyDaily(userId));
        String insight = nutritionInsightRepository.findTop20ByUserIdOrderByIdDesc(userId).stream().findFirst().map(NutritionInsight::getMessage)
                .orElse("Log meals to unlock nutrition intelligence.");
        return new SummaryResponse(latest.getAnalyticsDate(), latest.getNutritionScore(), latest.getConsistencyScore(), latest.getHydrationScore(),
                latest.getDietQualityScore(), Map.of("protein", 31, "carbs", 44, "fats", 25), currentStreak(userId), insight);
    }

    @Transactional(readOnly = true)
    public List<DailyAnalyticsResponse> daily(LocalDate from, LocalDate to) {
        UUID userId = SecuritySupport.currentUserId();
        return dailyAnalyticsRepository.findByUserIdAndAnalyticsDateBetweenOrderByAnalyticsDate(userId, from, to).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<InsightResponse> insights() {
        UUID userId = SecuritySupport.currentUserId();
        return nutritionInsightRepository.findTop20ByUserIdOrderByIdDesc(userId).stream()
                .map(insight -> new InsightResponse(insight.getInsightType(), insight.getTitle(), insight.getMessage(), insight.getSeverity(), insight.getEvidence()))
                .toList();
    }

    private void maybeGenerateInsight(UUID userId, LocalDate date, NutritionTotals totals, boolean lateNight, boolean skippedBreakfast) {
        if (totals.proteinG().doubleValue() < 80) {
            saveInsight(userId, "PROTEIN_TREND", "Protein intake is below target",
                    "Protein intake is below the platform target for this day.", "{\"proteinG\":" + totals.proteinG() + "}", "MEDIUM", date);
        }
        if (lateNight) {
            saveInsight(userId, "LATE_NIGHT_EATING", "Late-night calories detected",
                    "Most recent calories were consumed after 9PM.", "{\"hour\":\"after 21:00\"}", "LOW", date);
        }
        if (skippedBreakfast) {
            saveInsight(userId, "SKIPPED_BREAKFAST", "Breakfast consistency opportunity",
                    "No breakfast entry was observed before this meal event.", "{\"mealType\":\"not breakfast\"}", "LOW", date);
        }
    }

    private void saveInsight(UUID userId, String type, String title, String message, String evidence, String severity, LocalDate date) {
        NutritionInsight saved = nutritionInsightRepository.save(new NutritionInsight(userId, type, title, message, evidence, severity, date.minusDays(14), date));
        kafkaTemplate.send(KafkaTopics.INSIGHT_GENERATED, userId.toString(), EventEnvelope.of(
                "NutritionInsightGeneratedEvent",
                "analytics-service",
                UUID.randomUUID().toString(),
                new Events.NutritionInsightGeneratedEvent(userId, type, message, Map.of("evidence", evidence, "insightId", saved.getId().toString()))
        ));
    }

    private void cacheSummary(UUID userId, DailyAnalytics daily) {
        try {
            redisTemplate.opsForValue().set("analytics:daily:" + userId + ":" + daily.getAnalyticsDate(), objectMapper.writeValueAsString(toResponse(daily)));
            redisTemplate.delete("analytics:summary:" + userId);
        } catch (Exception ignored) {
            // PostgreSQL remains source of truth.
        }
    }

    private int currentStreak(UUID userId) {
        return dailyAnalyticsRepository.findByUserIdAndAnalyticsDateBetweenOrderByAnalyticsDate(userId, LocalDate.now().minusDays(100), LocalDate.now()).size();
    }

    private DailyAnalytics emptyDaily(UUID userId) {
        DailyAnalytics daily = new DailyAnalytics(userId, LocalDate.now());
        daily.apply(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                0, 0, 0, 0, 0, 0, BigDecimal.ZERO, false, 0);
        return daily;
    }

    private DailyAnalyticsResponse toResponse(DailyAnalytics daily) {
        return new DailyAnalyticsResponse(daily.getAnalyticsDate(), daily.getCalories(), daily.getProteinG(), daily.getCarbsG(), daily.getFatsG(),
                daily.getFiberG(), daily.getSugarG(), daily.getWaterMl(), daily.getNutritionScore(), daily.getMacroBalanceScore(),
                daily.getConsistencyScore(), daily.getHydrationScore(), daily.getDietQualityScore(), daily.isSkippedBreakfast());
    }

    private NutritionTotals totalsFromMap(Map<String, Object> map) {
        return new NutritionTotals(num(map, "calories"), num(map, "proteinG"), num(map, "carbsG"), num(map, "fatsG"),
                num(map, "fiberG"), num(map, "sugarG"), num(map, "sodiumMg"), number(map.get("waterMl")).intValue());
    }

    private BigDecimal num(Map<String, Object> map, String key) {
        return number(map.get(key));
    }

    private BigDecimal number(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(String.valueOf(value));
    }
}

