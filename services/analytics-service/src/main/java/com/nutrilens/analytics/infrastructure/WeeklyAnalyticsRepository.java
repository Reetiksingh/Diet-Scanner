package com.nutrilens.analytics.infrastructure;

import com.nutrilens.analytics.domain.WeeklyAnalytics;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyAnalyticsRepository extends JpaRepository<WeeklyAnalytics, UUID> {
}

