package com.nutrilens.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nutrilens.auth.application.AuthSessionService;
import com.nutrilens.auth.application.JwtKeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/.well-known/jwks.json",
                                "/api/v1/auth/development/login", "/api/v1/auth/refresh").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth.successHandler(successHandler))
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    JwtEncoder jwtEncoder(JwtKeyService jwtKeyService) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<SecurityContext>(new com.nimbusds.jose.jwk.JWKSet(jwtKeyService.rsaKey())));
    }

    @Bean
    JwtDecoder jwtDecoder(JwtKeyService jwtKeyService) throws Exception {
        return NimbusJwtDecoder.withPublicKey(jwtKeyService.rsaKey().toRSAPublicKey()).build();
    }

    @Bean
    AuthenticationSuccessHandler oauthSuccessHandler(AuthSessionService authSessionService) {
        return (request, response, authentication) -> {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            AuthSessionService.SessionTokens session = authSessionService.issueForOAuth(
                    token.getAuthorizedClientRegistrationId(),
                    token.getPrincipal().getAttributes(),
                    remoteAddress(request),
                    request.getHeader("User-Agent")
            );
            addCookie(response, "refresh_token", session.refreshToken(), session.refreshExpiresAt().getEpochSecond());
            addCookie(response, "refresh_family", session.familyId().toString(), session.refreshExpiresAt().getEpochSecond());
            response.sendRedirect("/auth/callback?access_token=" + session.tokenResponse().accessToken());
        };
    }

    private void addCookie(HttpServletResponse response, String name, String value, long expiresAtEpochSeconds) throws IOException {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Math.max(0, expiresAtEpochSeconds - java.time.Instant.now().getEpochSecond()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String remoteAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

