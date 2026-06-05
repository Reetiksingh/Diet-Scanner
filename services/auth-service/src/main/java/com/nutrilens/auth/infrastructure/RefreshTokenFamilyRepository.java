package com.nutrilens.auth.infrastructure;

import com.nutrilens.auth.domain.RefreshTokenFamily;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenFamilyRepository extends JpaRepository<RefreshTokenFamily, UUID> {
    Optional<RefreshTokenFamily> findByFamilyId(UUID familyId);
}

