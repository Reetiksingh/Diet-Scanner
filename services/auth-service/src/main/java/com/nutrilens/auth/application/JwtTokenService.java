package com.nutrilens.auth.application;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration accessTokenTtl;

    public JwtTokenService(JwtEncoder jwtEncoder,
                           @Value("${nutrilens.auth.issuer}") String issuer,
                           @Value("${nutrilens.auth.access-token-ttl-minutes:15}") long accessTokenTtlMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    }

    public IssuedAccessToken issue(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTokenTtl);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(userId.toString())
                .id(UUID.randomUUID().toString())
                .claim("email", email)
                .claim("scope", "openid profile email")
                .build();
        String value = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new IssuedAccessToken(value, expiresAt);
    }

    public record IssuedAccessToken(String tokenValue, Instant expiresAt) {
    }
}

