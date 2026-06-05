package com.nutrilens.leaderboard.application;

import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.security.SecuritySupport;
import com.nutrilens.leaderboard.api.dto.LeaderboardDtos.LeaderboardResponse;
import com.nutrilens.leaderboard.api.dto.LeaderboardDtos.RankResponse;
import com.nutrilens.leaderboard.api.dto.LeaderboardDtos.RankingItem;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardService {
    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public LeaderboardService(StringRedisTemplate redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.DAILY_COMPUTED, groupId = "leaderboard-service")
    public void onDailyAnalytics(EventEnvelope<?> envelope) {
        LinkedHashMap<?, ?> payload = (LinkedHashMap<?, ?>) envelope.payload();
        UUID userId = UUID.fromString(String.valueOf(payload.get("userId")));
        double nutritionScore = Double.parseDouble(String.valueOf(payload.get("nutritionScore")));
        double consistencyScore = Double.parseDouble(String.valueOf(payload.get("consistencyScore")));
        update("leaderboard:global:nutrition-score:weekly", userId, nutritionScore, envelope.correlationId());
        update("leaderboard:global:consistency-score:weekly", userId, consistencyScore, envelope.correlationId());
        update("leaderboard:country:IN:nutrition-score:weekly", userId, nutritionScore, envelope.correlationId());
        update("leaderboard:global:nutrition-score:monthly", userId, nutritionScore, envelope.correlationId());
    }

    public LeaderboardResponse leaderboard(String scope, String country, String metric, String period, int limit) {
        String key = key(scope, country, metric, period);
        var tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, Math.max(0, limit - 1));
        List<RankingItem> items = tuples == null ? List.of() : tuples.stream()
                .map(tuple -> new RankingItem(redisTemplate.opsForZSet().reverseRank(key, tuple.getValue()) + 1,
                        UUID.fromString(tuple.getValue()), "NutriLens User", tuple.getScore(), country == null ? "GLOBAL" : country))
                .toList();
        return new LeaderboardResponse(metric, period, items);
    }

    public RankResponse myRank(String metric, String period) {
        UUID userId = SecuritySupport.currentUserId();
        String key = key("global", null, metric, period);
        Long rank = redisTemplate.opsForZSet().reverseRank(key, userId.toString());
        Double score = redisTemplate.opsForZSet().score(key, userId.toString());
        return new RankResponse(key, userId, rank == null ? -1 : rank + 1, score == null ? 0 : score);
    }

    private void update(String key, UUID userId, double score, String correlationId) {
        redisTemplate.opsForZSet().add(key, userId.toString(), score);
        Long rank = redisTemplate.opsForZSet().reverseRank(key, userId.toString());
        kafkaTemplate.send(KafkaTopics.LEADERBOARD_UPDATED, userId.toString(), EventEnvelope.of(
                "LeaderboardUpdatedEvent",
                "leaderboard-service",
                correlationId,
                new Events.LeaderboardUpdatedEvent(key, userId, score, rank == null ? -1 : rank + 1)
        ));
    }

    private String key(String scope, String country, String metric, String period) {
        if ("country".equalsIgnoreCase(scope)) {
            return "leaderboard:country:" + country + ":" + metric + ":" + period;
        }
        return "leaderboard:global:" + metric + ":" + period;
    }
}

