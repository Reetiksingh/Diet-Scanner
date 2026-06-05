package com.nutrilens.gateway.config;

import java.util.List;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayRoutes {
    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**", "/.well-known/jwks.json")
                        .filters(f -> f.stripPrefix(0))
                        .uri("http://auth-service:8080"))
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .uri("http://user-service:8080"))
                .route("nutrition-service", r -> r.path("/api/v1/nutrition/**")
                        .uri("http://nutrition-service:8080"))
                .route("analytics-service", r -> r.path("/api/v1/analytics/**")
                        .uri("http://analytics-service:8080"))
                .route("recommendation-service", r -> r.path("/api/v1/recommendations/**")
                        .uri("http://recommendation-service:8080"))
                .route("leaderboard-service", r -> r.path("/api/v1/leaderboards/**", "/ws/leaderboards/**")
                        .uri("http://leaderboard-service:8080"))
                .route("achievement-service", r -> r.path("/api/v1/achievements/**")
                        .uri("http://achievement-service:8080"))
                .route("notification-service", r -> r.path("/api/v1/notifications/**", "/ws/notifications/**")
                        .uri("http://notification-service:8080"))
                .build();
    }

    @Bean
    List<ServiceProbe> serviceProbes() {
        return List.of(
                new ServiceProbe("auth-service", "http://auth-service:8080/actuator/health"),
                new ServiceProbe("user-service", "http://user-service:8080/actuator/health"),
                new ServiceProbe("nutrition-service", "http://nutrition-service:8080/actuator/health"),
                new ServiceProbe("analytics-service", "http://analytics-service:8080/actuator/health"),
                new ServiceProbe("recommendation-service", "http://recommendation-service:8080/actuator/health"),
                new ServiceProbe("leaderboard-service", "http://leaderboard-service:8080/actuator/health"),
                new ServiceProbe("achievement-service", "http://achievement-service:8080/actuator/health"),
                new ServiceProbe("notification-service", "http://notification-service:8080/actuator/health")
        );
    }

    @Bean
    KeyResolver principalOrIpKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .map(principal -> "principal:" + principal.getName())
                .switchIfEmpty(Mono.just("ip:" + exchange.getRequest().getRemoteAddress()));
    }

    public record ServiceProbe(String service, String healthUrl) {
    }
}
