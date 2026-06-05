package com.nutrilens.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token_families", schema = "auth")
public class RefreshTokenFamily {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "family_id", nullable = false)
    private UUID familyId;
    @Column(name = "current_token_hash", nullable = false)
    private String currentTokenHash;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    @Column(name = "reuse_detected_at")
    private Instant reuseDetectedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Version
    private long version;

    protected RefreshTokenFamily() {
    }

    public RefreshTokenFamily(UUID userId, UUID familyId, String currentTokenHash, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.familyId = familyId;
        this.currentTokenHash = currentTokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    public boolean accepts(String tokenHash) {
        return revokedAt == null && expiresAt.isAfter(Instant.now()) && currentTokenHash.equals(tokenHash);
    }

    public void rotate(String newTokenHash, Instant newExpiry) {
        this.currentTokenHash = newTokenHash;
        this.expiresAt = newExpiry;
    }

    public void revoke() {
        this.revokedAt = Instant.now();
    }

    public void markReuseDetected() {
        this.reuseDetectedAt = Instant.now();
        revoke();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}

