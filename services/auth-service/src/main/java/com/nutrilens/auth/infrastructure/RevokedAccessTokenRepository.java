package com.nutrilens.auth.infrastructure;

import com.nutrilens.auth.domain.RevokedAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessToken, String> {
}

