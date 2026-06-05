package com.nutrilens.auth.api;

import com.nutrilens.auth.api.dto.AuthDtos.DevLoginRequest;
import com.nutrilens.auth.api.dto.AuthDtos.TokenResponse;
import com.nutrilens.auth.api.dto.AuthDtos.UserSessionResponse;
import com.nutrilens.auth.application.AuthSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthSessionService authSessionService;

    public AuthController(AuthSessionService authSessionService) {
        this.authSessionService = authSessionService;
    }

    @PostMapping("/development/login")
    public TokenResponse developmentLogin(@Valid @RequestBody DevLoginRequest request, HttpServletResponse response) {
        AuthSessionService.SessionTokens session = authSessionService.issueForDevelopment(
                blankToDefault(request.email(), "developer@nutrilens.local"),
                blankToDefault(request.displayName(), "NutriLens Developer"),
                blankToDefault(request.countryCode(), "IN")
        );
        attachRefreshCookies(response, session);
        return session.tokenResponse();
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        UUID familyId = UUID.fromString(cookie(request, "refresh_family"));
        String refreshToken = cookie(request, "refresh_token");
        AuthSessionService.SessionTokens session = authSessionService.refresh(familyId, refreshToken);
        attachRefreshCookies(response, session);
        return session.tokenResponse();
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal Jwt jwt) {
        UUID familyId = UUID.fromString(cookie(request, "refresh_family"));
        UUID userId = jwt == null ? null : UUID.fromString(jwt.getSubject());
        String jti = jwt == null ? null : jwt.getId();
        Instant expiresAt = jwt == null ? null : jwt.getExpiresAt();
        authSessionService.logout(familyId, jti, userId, expiresAt);
        expireCookie(response, "refresh_token");
        expireCookie(response, "refresh_family");
    }

    @GetMapping("/me")
    public UserSessionResponse me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return new UserSessionResponse(null, null, "anonymous");
        }
        return new UserSessionResponse(UUID.fromString(jwt.getSubject()), jwt.getClaimAsString("email"), jwt.getIssuer().toString());
    }

    private void attachRefreshCookies(HttpServletResponse response, AuthSessionService.SessionTokens session) {
        Duration maxAge = Duration.between(Instant.now(), session.refreshExpiresAt());
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", session.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(maxAge)
                .build();
        ResponseCookie familyCookie = ResponseCookie.from("refresh_family", session.familyId().toString())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, familyCookie.toString());
    }

    private void expireCookie(HttpServletResponse response, String name) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO)
                .build()
                .toString());
    }

    private String cookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            throw new IllegalArgumentException("Missing cookie: " + name);
        }
        for (var cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new IllegalArgumentException("Missing cookie: " + name);
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

