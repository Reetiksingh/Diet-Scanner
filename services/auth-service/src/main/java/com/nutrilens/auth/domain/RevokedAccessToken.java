package com.nutrilens.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "revoked_access_tokens", schema = "auth")
public class RevokedAccessToken {
    @Id
    private String jti;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at", nullable = false)
    private Instant revokedAt;

    protected RevokedAccessToken() {
    }

    public RevokedAccessToken(String jti, UUID userId, Instant expiresAt) {
        this.jti = jti;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.revokedAt = Instant.now();
    }
}

