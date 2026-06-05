package com.nutrilens.gateway.api;

import com.nutrilens.gateway.config.GatewayRoutes.ServiceProbe;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/health")
public class HealthAggregationController {
    private final WebClient webClient;
    private final List<ServiceProbe> serviceProbes;

    public HealthAggregationController(WebClient.Builder builder, List<ServiceProbe> serviceProbes) {
        this.webClient = builder.build();
        this.serviceProbes = serviceProbes;
    }

    @GetMapping("/services")
    Mono<List<ServiceHealth>> services() {
        return Flux.fromIterable(serviceProbes)
                .flatMap(probe -> webClient.get()
                        .uri(probe.healthUrl())
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(body -> new ServiceHealth(probe.service(), "UP"))
                        .onErrorReturn(new ServiceHealth(probe.service(), "DOWN")))
                .collectList();
    }

    public record ServiceHealth(String service, String status) {
    }
}

