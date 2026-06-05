package com.nutrilens.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "users")
public class UserAccount {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "display_name", nullable = false)
    private String displayName;
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Column(name = "country_code")
    private String countryCode;
    @Column(nullable = false)
    private String timezone;
    @Column(nullable = false)
    private String status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    private long version;

    protected UserAccount() {
    }

    public UserAccount(UUID id, String email, String displayName, String countryCode) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.countryCode = countryCode;
        this.timezone = "Asia/Kolkata";
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateProfile(String displayName, String avatarUrl, String countryCode, String timezone) {
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName;
        }
        this.avatarUrl = avatarUrl;
        if (countryCode != null && !countryCode.isBlank()) {
            this.countryCode = countryCode;
        }
        if (timezone != null && !timezone.isBlank()) {
            this.timezone = timezone;
        }
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getStatus() {
        return status;
    }
}

