package com.nutrilens.recommendation.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.security.SecuritySupport;
import com.nutrilens.recommendation.api.dto.RecommendationDtos.RecommendationBundle;
import com.nutrilens.recommendation.api.dto.RecommendationDtos.RecommendationResponse;
import com.nutrilens.recommendation.api.dto.RecommendationDtos.ReportResponse;
import com.nutrilens.recommendation.domain.Recommendation;
import com.nutrilens.recommendation.infrastructure.RecommendationRepository;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationEngine recommendationEngine;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RecommendationService(RecommendationRepository recommendationRepository, RecommendationEngine recommendationEngine,
                                 KafkaTemplate<String, Object> kafkaTemplate, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationEngine = recommendationEngine;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.DAILY_COMPUTED, groupId = "recommendation-service")
    @Transactional
    public void onDailyAnalytics(EventEnvelope<?> envelope) {
        LinkedHashMap<?, ?> payload = (LinkedHashMap<?, ?>) envelope.payload();
        UUID userId = UUID.fromString(String.valueOf(payload.get("userId")));
        LocalDate date = LocalDate.parse(String.valueOf(payload.get("date")));
        int nutritionScore = number(payload.get("nutritionScore"));
        int consistencyScore = number(payload.get("consistencyScore"));
        int hydrationScore = number(payload.get("hydrationScore"));
        List<Recommendation> generated = recommendationEngine.fromDailyAnalytics(userId, date, nutritionScore, consistencyScore, hydrationScore);
        recommendationRepository.saveAll(generated);
        redisTemplate.delete("recommendations:current:" + userId);
        for (Recommendation recommendation : generated) {
            kafkaTemplate.send(KafkaTopics.RECOMMENDATION_GENERATED, userId.toString(), EventEnvelope.of(
                    "RecommendationGeneratedEvent",
                    "recommendation-service",
                    envelope.correlationId(),
                    new Events.RecommendationGeneratedEvent(userId, recommendation.getId(), recommendation.getRecommendationType(), recommendation.getTitle())
            ));
        }
    }

    @Transactional(readOnly = true)
    public RecommendationBundle current() {
        UUID userId = SecuritySupport.currentUserId();
        String cacheKey = "recommendations:current:" + userId;
        List<RecommendationResponse> responses = recommendationRepository.findTop20ByUserIdAndStatusOrderByPriorityDesc(userId, "ACTIVE").stream()
                .map(this::toResponse)
                .toList();
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(responses));
        } catch (Exception ignored) {
        }
        return new RecommendationBundle(responses);
    }

    public ReportResponse weekly() {
        return new ReportResponse("weekly", "Weekly summary is generated from deterministic nutrition, consistency, and hydration scores.", current().recommendations());
    }

    public ReportResponse monthly() {
        return new ReportResponse("monthly", "Monthly summary highlights recurring nutrition behavior and score movement.", current().recommendations());
    }

    private RecommendationResponse toResponse(Recommendation recommendation) {
        return new RecommendationResponse(recommendation.getId(), recommendation.getRecommendationType(), recommendation.getTitle(),
                recommendation.getBody(), recommendation.getEvidence(), recommendation.getPriority());
    }

    private int number(Object value) {
        return value == null ? 0 : Integer.parseInt(String.valueOf(value));
    }
}

