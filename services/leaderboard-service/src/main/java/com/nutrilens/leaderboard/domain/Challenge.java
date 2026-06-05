package com.nutrilens.leaderboard.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "challenges", schema = "gamification")
public class Challenge {
    @Id
    private UUID id;
    private String code;
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(name = "challenge_type")
    private String challengeType;
    private String metric;
    @Column(name = "starts_at")
    private Instant startsAt;
    @Column(name = "ends_at")
    private Instant endsAt;
    private String status;

    protected Challenge() {
    }

    public Challenge(String code, String name, String description, String challengeType, String metric, Instant startsAt, Instant endsAt) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.description = description;
        this.challengeType = challengeType;
        this.metric = metric;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = "ACTIVE";
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getMetric() { return metric; }
}

