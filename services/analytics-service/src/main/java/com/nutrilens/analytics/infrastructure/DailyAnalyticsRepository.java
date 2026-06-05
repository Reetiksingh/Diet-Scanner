package com.nutrilens.analytics.infrastructure;

import com.nutrilens.analytics.domain.DailyAnalytics;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyAnalyticsRepository extends JpaRepository<DailyAnalytics, UUID> {
    Optional<DailyAnalytics> findByUserIdAndAnalyticsDate(UUID userId, LocalDate analyticsDate);
    List<DailyAnalytics> findByUserIdAndAnalyticsDateBetweenOrderByAnalyticsDate(UUID userId, LocalDate from, LocalDate to);
}

