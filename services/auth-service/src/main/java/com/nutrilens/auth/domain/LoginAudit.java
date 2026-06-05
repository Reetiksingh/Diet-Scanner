package com.nutrilens.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "login_audit", schema = "auth")
public class LoginAudit {
    @Id
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    private String provider;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "user_agent")
    private String userAgent;
    private boolean success;
    @Column(name = "created_at")
    private Instant createdAt;

    protected LoginAudit() {
    }

    public LoginAudit(UUID userId, String provider, String ipAddress, String userAgent, boolean success) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.provider = provider;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.success = success;
        this.createdAt = Instant.now();
    }
}

