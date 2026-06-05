package com.nutrilens.nutrition.infrastructure;

import com.nutrilens.nutrition.domain.NutritionRecord;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionRecordRepository extends JpaRepository<NutritionRecord, UUID> {
    Optional<NutritionRecord> findByUserIdAndRecordDate(UUID userId, LocalDate recordDate);
}

