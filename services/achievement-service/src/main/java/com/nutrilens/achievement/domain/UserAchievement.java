package com.nutrilens.achievement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_achievements", schema = "gamification")
public class UserAchievement {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "achievement_id")
    private UUID achievementId;
    @Column(name = "unlocked_at")
    private Instant unlockedAt;
    @Column(name = "event_id")
    private UUID eventId;

    protected UserAchievement() {
    }

    public UserAchievement(UUID userId, UUID achievementId, UUID eventId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.achievementId = achievementId;
        this.eventId = eventId;
        this.unlockedAt = Instant.now();
    }

    public UUID getAchievementId() {
        return achievementId;
    }
}
