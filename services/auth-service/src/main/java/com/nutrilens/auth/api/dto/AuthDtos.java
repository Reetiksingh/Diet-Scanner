package com.nutrilens.auth.api.dto;

import java.time.Instant;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record TokenResponse(String accessToken, Instant accessTokenExpiresAt, UUID userId, String tokenType) {
    }

    public record UserSessionResponse(UUID userId, String email, String issuer) {
    }

    public record DevLoginRequest(String email, String displayName, String countryCode) {
    }
}

