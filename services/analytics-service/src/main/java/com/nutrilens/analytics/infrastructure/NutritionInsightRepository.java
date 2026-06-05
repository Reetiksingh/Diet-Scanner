package com.nutrilens.analytics.infrastructure;

import com.nutrilens.analytics.domain.NutritionInsight;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionInsightRepository extends JpaRepository<NutritionInsight, UUID> {
    List<NutritionInsight> findTop20ByUserIdOrderByIdDesc(UUID userId);
}

