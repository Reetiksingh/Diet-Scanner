package com.nutrilens.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_profiles", schema = "users")
public class UserProfile {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    private Integer age;
    @Column(name = "height_cm")
    private BigDecimal heightCm;
    @Column(name = "weight_kg")
    private BigDecimal weightKg;
    private String gender;
    @Column(name = "activity_level")
    private String activityLevel;
    @Column(name = "dietary_preference")
    private String dietaryPreference;
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected UserProfile() {
    }

    public UserProfile(UUID userId) {
        this.userId = userId;
        this.activityLevel = "MODERATE";
        this.dietaryPreference = "BALANCED";
        this.updatedAt = Instant.now();
    }

    public void update(Integer age, BigDecimal heightCm, BigDecimal weightKg, String gender, String activityLevel, String dietaryPreference) {
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.dietaryPreference = dietaryPreference;
        this.updatedAt = Instant.now();
    }

    public UUID getUserId() {
        return userId;
    }

    public Integer getAge() {
        return age;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public String getGender() {
        return gender;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }
}

