package com.nutrilens.recommendation.infrastructure;

import com.nutrilens.recommendation.domain.Recommendation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findTop20ByUserIdAndStatusOrderByPriorityDesc(UUID userId, String status);
}

