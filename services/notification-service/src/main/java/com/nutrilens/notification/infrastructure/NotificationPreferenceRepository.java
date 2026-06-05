package com.nutrilens.notification.infrastructure;

import com.nutrilens.notification.domain.NotificationPreference;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
}

