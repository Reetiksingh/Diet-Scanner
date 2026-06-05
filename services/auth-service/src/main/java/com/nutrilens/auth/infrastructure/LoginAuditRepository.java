package com.nutrilens.auth.infrastructure;

import com.nutrilens.auth.domain.LoginAudit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAuditRepository extends JpaRepository<LoginAudit, UUID> {
}

