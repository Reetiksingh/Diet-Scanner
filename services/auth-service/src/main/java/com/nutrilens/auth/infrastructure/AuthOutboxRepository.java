package com.nutrilens.auth.infrastructure;

import com.nutrilens.auth.domain.AuthOutboxEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthOutboxRepository extends JpaRepository<AuthOutboxEvent, UUID> {
}

