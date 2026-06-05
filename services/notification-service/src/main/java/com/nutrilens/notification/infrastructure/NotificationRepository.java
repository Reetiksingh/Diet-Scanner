package com.nutrilens.notification.infrastructure;

import com.nutrilens.notification.domain.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findTop50ByUserIdOrderByCreatedAtDesc(UUID userId);
}

