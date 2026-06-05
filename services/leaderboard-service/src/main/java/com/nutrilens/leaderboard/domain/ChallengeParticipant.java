package com.nutrilens.leaderboard.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "challenge_participants", schema = "gamification")
public class ChallengeParticipant {
    @Id
    private UUID id;
    @Column(name = "challenge_id")
    private UUID challengeId;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "joined_at")
    private Instant joinedAt;
    private BigDecimal progress;
    @Column(name = "completed_at")
    private Instant completedAt;

    protected ChallengeParticipant() {
    }

    public ChallengeParticipant(UUID challengeId, UUID userId) {
        this.id = UUID.randomUUID();
        this.challengeId = challengeId;
        this.userId = userId;
        this.joinedAt = Instant.now();
        this.progress = BigDecimal.ZERO;
    }
}

