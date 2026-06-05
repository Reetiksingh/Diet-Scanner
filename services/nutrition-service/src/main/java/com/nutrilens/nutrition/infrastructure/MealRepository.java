package com.nutrilens.nutrition.infrastructure;

import com.nutrilens.nutrition.domain.Meal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, UUID> {
    List<Meal> findByUserIdAndConsumedAtBetweenOrderByConsumedAtDesc(UUID userId, Instant from, Instant to);
}

