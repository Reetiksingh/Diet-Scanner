package com.nutrilens.achievement.infrastructure;

import com.nutrilens.achievement.domain.Achievement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    List<Achievement> findByActiveTrue();
    Optional<Achievement> findByCode(String code);
}

