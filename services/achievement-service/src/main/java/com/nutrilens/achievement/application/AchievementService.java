package com.nutrilens.achievement.application;

import com.nutrilens.achievement.api.dto.AchievementDtos.AchievementList;
import com.nutrilens.achievement.api.dto.AchievementDtos.AchievementResponse;
import com.nutrilens.achievement.domain.Achievement;
import com.nutrilens.achievement.domain.UserAchievement;
import com.nutrilens.achievement.infrastructure.AchievementRepository;
import com.nutrilens.achievement.infrastructure.UserAchievementRepository;
import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import com.nutrilens.common.security.SecuritySupport;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StringRedisTemplate redisTemplate;

    public AchievementService(AchievementRepository achievementRepository, UserAchievementRepository userAchievementRepository,
                              KafkaTemplate<String, Object> kafkaTemplate, StringRedisTemplate redisTemplate) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(topics = KafkaTopics.DAILY_COMPUTED, groupId = "achievement-service")
    @Transactional
    public void onDailyAnalytics(EventEnvelope<?> envelope) {
        LinkedHashMap<?, ?> payload = (LinkedHashMap<?, ?>) envelope.payload();
        UUID userId = UUID.fromString(String.valueOf(payload.get("userId")));
        int nutritionScore = number(payload.get("nutritionScore"));
        int consistencyScore = number(payload.get("consistencyScore"));
        int hydrationScore = number(payload.get("hydrationScore"));
        int streakDays = number(payload.get("streakDays"));
        unlockIf(userId, "NUTRITION_CHAMPION", nutritionScore >= 90, envelope.eventId(), envelope.correlationId());
        unlockIf(userId, "CONSISTENCY_MASTER", consistencyScore >= 85, envelope.eventId(), envelope.correlationId());
        unlockIf(userId, "HYDRATION_HERO", hydrationScore >= 90, envelope.eventId(), envelope.correlationId());
        unlockIf(userId, "THIRTY_DAY_STREAK", streakDays >= 30, envelope.eventId(), envelope.correlationId());
        redisTemplate.opsForValue().set("streak:" + userId + ":logging", String.valueOf(streakDays));
    }

    @Transactional(readOnly = true)
    public AchievementList all() {
        UUID userId = SecuritySupport.currentUserId();
        Set<UUID> unlocked = userAchievementRepository.findByUserId(userId).stream()
                .map(UserAchievement::getAchievementId)
                .collect(Collectors.toSet());
        return new AchievementList(achievementRepository.findByActiveTrue().stream()
                .map(achievement -> new AchievementResponse(achievement.getId(), achievement.getCode(), achievement.getName(), achievement.getDescription(), unlocked.contains(achievement.getId())))
                .toList());
    }

    private void unlockIf(UUID userId, String code, boolean condition, UUID eventId, String correlationId) {
        if (!condition) {
            return;
        }
        achievementRepository.findByCode(code).ifPresent(achievement -> {
            if (userAchievementRepository.findByUserIdAndAchievementId(userId, achievement.getId()).isEmpty()) {
                userAchievementRepository.save(new UserAchievement(userId, achievement.getId(), eventId));
                kafkaTemplate.send(KafkaTopics.ACHIEVEMENT_UNLOCKED, userId.toString(), EventEnvelope.of(
                        "AchievementUnlockedEvent",
                        "achievement-service",
                        correlationId,
                        new Events.AchievementUnlockedEvent(userId, achievement.getId(), achievement.getCode(), achievement.getName(), java.time.Instant.now())
                ));
            }
        });
    }

    private int number(Object value) {
        return value == null ? 0 : Integer.parseInt(String.valueOf(value));
    }
}
