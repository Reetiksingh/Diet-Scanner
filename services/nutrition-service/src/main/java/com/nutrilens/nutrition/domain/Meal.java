package com.nutrilens.nutrition.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "meals", schema = "nutrition")
public class Meal {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "meal_type", nullable = false)
    private String mealType;
    @Column(name = "consumed_at", nullable = false)
    private Instant consumedAt;
    @Column(nullable = false)
    private String timezone;
    private String notes;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Version
    private long version;
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealEntry> entries = new ArrayList<>();

    protected Meal() {
    }

    public Meal(UUID userId, String mealType, Instant consumedAt, String timezone, String notes) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.mealType = mealType;
        this.consumedAt = consumedAt;
        this.timezone = timezone;
        this.notes = notes;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void addEntry(MealEntry entry) {
        entries.add(entry);
        entry.attachMeal(this);
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getMealType() { return mealType; }
    public Instant getConsumedAt() { return consumedAt; }
    public String getTimezone() { return timezone; }
    public List<MealEntry> getEntries() { return entries; }
}

