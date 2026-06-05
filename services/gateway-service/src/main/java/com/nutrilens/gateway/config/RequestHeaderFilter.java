package com.nutrilens.gateway.config;

import java.security.Principal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestHeaderFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestHeaderFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        String finalRequestId = requestId;
        return exchange.getPrincipal()
                .defaultIfEmpty(new Principal() {
                    @Override
                    public String getName() {
                        return "";
                    }
                })
                .flatMap(principal -> {
                    var request = exchange.getRequest().mutate()
                            .header("X-Request-Id", finalRequestId);
                    if (principal instanceof JwtAuthenticationToken jwtAuthentication) {
                        Jwt jwt = jwtAuthentication.getToken();
                        request.header("X-User-Id", jwt.getSubject());
                        request.header("X-User-Email", jwt.getClaimAsString("email"));
                    }
                    log.info("gateway_request method={} path={} requestId={}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath(),
                            finalRequestId);
                    return chain.filter(exchange.mutate().request(request.build()).build());
                });
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

