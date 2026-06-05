package com.nutrilens.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "oauth_identities", schema = "auth",
        uniqueConstraints = @UniqueConstraint(name = "uk_oauth_provider_subject", columnNames = {"provider", "provider_subject"}))
public class OAuthIdentity {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String provider;
    @Column(name = "provider_subject", nullable = false)
    private String providerSubject;
    @Column(nullable = false)
    private String email;
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected OAuthIdentity() {
    }

    public OAuthIdentity(UUID userId, String provider, String providerSubject, String email, boolean emailVerified) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.email = email;
        this.emailVerified = emailVerified;
        this.createdAt = Instant.now();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}

