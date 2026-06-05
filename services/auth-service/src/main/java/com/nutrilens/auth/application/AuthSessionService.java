package com.nutrilens.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrilens.auth.api.dto.AuthDtos.TokenResponse;
import com.nutrilens.auth.domain.LoginAudit;
import com.nutrilens.auth.domain.OAuthIdentity;
import com.nutrilens.auth.domain.RefreshTokenFamily;
import com.nutrilens.auth.infrastructure.LoginAuditRepository;
import com.nutrilens.auth.infrastructure.OAuthIdentityRepository;
import com.nutrilens.auth.infrastructure.RefreshTokenFamilyRepository;
import com.nutrilens.auth.infrastructure.RevokedAccessTokenRepository;
import com.nutrilens.common.events.EventEnvelope;
import com.nutrilens.common.events.Events;
import com.nutrilens.common.events.KafkaTopics;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthSessionService {
    private final OAuthIdentityRepository identityRepository;
    private final RefreshTokenFamilyRepository tokenFamilyRepository;
    private final LoginAuditRepository loginAuditRepository;
    private final JwtTokenService jwtTokenService;
    private final TokenHashingService hashingService;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Duration refreshTokenTtl;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthSessionService(
            OAuthIdentityRepository identityRepository,
            RefreshTokenFamilyRepository tokenFamilyRepository,
            LoginAuditRepository loginAuditRepository,
            JwtTokenService jwtTokenService,
            TokenHashingService hashingService,
            RevokedAccessTokenRepository revokedAccessTokenRepository,
            StringRedisTemplate redisTemplate,
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${nutrilens.auth.refresh-token-ttl-days:30}") long refreshTokenTtlDays) {
        this.identityRepository = identityRepository;
        this.tokenFamilyRepository = tokenFamilyRepository;
        this.loginAuditRepository = loginAuditRepository;
        this.jwtTokenService = jwtTokenService;
        this.hashingService = hashingService;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.refreshTokenTtl = Duration.ofDays(refreshTokenTtlDays);
    }

    @Transactional
    public SessionTokens issueForOAuth(String provider, Map<String, Object> attributes, String ipAddress, String userAgent) {
        String providerSubject = attribute(attributes, "sub", "id", "email");
        String email = attribute(attributes, "email", "login");
        String displayName = attribute(attributes, "name", "login", "email");
        boolean emailVerified = Boolean.parseBoolean(String.valueOf(attributes.getOrDefault("email_verified", "true")));

        OAuthIdentity identity = identityRepository.findByProviderAndProviderSubject(provider, providerSubject)
                .orElseGet(() -> {
                    UUID userId = UUID.randomUUID();
                    OAuthIdentity created = identityRepository.save(new OAuthIdentity(userId, provider, providerSubject, email, emailVerified));
                    kafkaTemplate.send(KafkaTopics.USER_REGISTERED, userId.toString(), EventEnvelope.of(
                            "UserRegisteredEvent",
                            "auth-service",
                            UUID.randomUUID().toString(),
                            new Events.UserRegisteredEvent(userId, email, displayName, "IN")
                    ));
                    return created;
                });

        loginAuditRepository.save(new LoginAudit(identity.getUserId(), provider, ipAddress, userAgent, true));
        kafkaTemplate.send(KafkaTopics.USER_LOGGED_IN, identity.getUserId().toString(), EventEnvelope.of(
                "UserLoggedInEvent",
                "auth-service",
                UUID.randomUUID().toString(),
                new Events.UserLoggedInEvent(identity.getUserId(), provider, Instant.now())
        ));
        return createSession(identity.getUserId(), identity.getEmail());
    }

    @Transactional
    public SessionTokens issueForDevelopment(String email, String displayName, String countryCode) {
        UUID userId = UUID.nameUUIDFromBytes(("dev:" + email).getBytes());
        OAuthIdentity identity = identityRepository.findByProviderAndProviderSubject("development", email)
                .orElseGet(() -> {
                    OAuthIdentity created = identityRepository.save(new OAuthIdentity(userId, "development", email, email, true));
                    kafkaTemplate.send(KafkaTopics.USER_REGISTERED, userId.toString(), EventEnvelope.of(
                            "UserRegisteredEvent",
                            "auth-service",
                            UUID.randomUUID().toString(),
                            new Events.UserRegisteredEvent(userId, email, displayName, countryCode)
                    ));
                    return created;
                });
        return createSession(identity.getUserId(), identity.getEmail());
    }

    @Transactional
    public SessionTokens refresh(UUID familyId, String rawRefreshToken) {
        RefreshTokenFamily family = tokenFamilyRepository.findByFamilyId(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token family was not found"));
        String presentedHash = hashingService.hash(rawRefreshToken);
        if (!family.accepts(presentedHash)) {
            family.markReuseDetected();
            throw new IllegalArgumentException("Refresh token reuse detected");
        }
        String newRefreshToken = secureToken();
        family.rotate(hashingService.hash(newRefreshToken), Instant.now().plus(refreshTokenTtl));
        tokenFamilyRepository.save(family);
        cacheRefreshToken(family.getUserId(), family.getFamilyId(), family.getExpiresAt());
        JwtTokenService.IssuedAccessToken accessToken = jwtTokenService.issue(family.getUserId(), "user@nutrilens.local");
        return new SessionTokens(new TokenResponse(accessToken.tokenValue(), accessToken.expiresAt(), family.getUserId(), "Bearer"),
                family.getFamilyId(), newRefreshToken, family.getExpiresAt());
    }

    @Transactional
    public void logout(UUID familyId, String jti, UUID userId, Instant accessTokenExpiresAt) {
        tokenFamilyRepository.findByFamilyId(familyId).ifPresent(RefreshTokenFamily::revoke);
        if (jti != null && accessTokenExpiresAt != null && userId != null) {
            revokedAccessTokenRepository.save(new com.nutrilens.auth.domain.RevokedAccessToken(jti, userId, accessTokenExpiresAt));
            redisTemplate.opsForValue().set("auth:blacklist:" + jti, "revoked", Duration.between(Instant.now(), accessTokenExpiresAt));
        }
        kafkaTemplate.send(KafkaTopics.SESSION_REVOKED, userId == null ? "unknown" : userId.toString(), EventEnvelope.of(
                "SessionRevokedEvent",
                "auth-service",
                UUID.randomUUID().toString(),
                new Events.SessionRevokedEvent(userId, familyId, "USER_LOGOUT")
        ));
    }

    private SessionTokens createSession(UUID userId, String email) {
        JwtTokenService.IssuedAccessToken accessToken = jwtTokenService.issue(userId, email);
        UUID familyId = UUID.randomUUID();
        String refreshToken = secureToken();
        Instant refreshExpiresAt = Instant.now().plus(refreshTokenTtl);
        tokenFamilyRepository.save(new RefreshTokenFamily(userId, familyId, hashingService.hash(refreshToken), refreshExpiresAt));
        cacheRefreshToken(userId, familyId, refreshExpiresAt);
        return new SessionTokens(new TokenResponse(accessToken.tokenValue(), accessToken.expiresAt(), userId, "Bearer"),
                familyId, refreshToken, refreshExpiresAt);
    }

    private void cacheRefreshToken(UUID userId, UUID familyId, Instant expiresAt) {
        try {
            String key = "auth:refresh:" + userId + ":" + familyId;
            String value = objectMapper.writeValueAsString(Map.of("userId", userId.toString(), "familyId", familyId.toString()));
            redisTemplate.opsForValue().set(key, value, Duration.between(Instant.now(), expiresAt));
        } catch (Exception ignored) {
            // Redis is a performance mirror; PostgreSQL remains the durable source.
        }
    }

    private String secureToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String attribute(Map<String, Object> attributes, String... keys) {
        for (String key : keys) {
            Object value = attributes.get(key);
            if (value != null && !String.valueOf(value).isBlank()) {
                return String.valueOf(value);
            }
        }
        return UUID.randomUUID().toString();
    }

    public record SessionTokens(TokenResponse tokenResponse, UUID familyId, String refreshToken, Instant refreshExpiresAt) {
    }
}
