package duoc.cl.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
public class GatewayConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Bean
    @Order(-1)
    public GlobalFilter requestIdFilter() {
        return (exchange, chain) -> {
            String requestId = UUID.randomUUID().toString().substring(0, 8);
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-Request-Id", requestId)
                    .build();
            ServerWebExchange mutatedExchange = exchange.mutate().request(mutated).build();
            mutatedExchange.getResponse().getHeaders().add("X-Request-Id", requestId);
            return chain.filter(mutatedExchange);
        };
    }

    @Bean
    @Order(-2)
    public GlobalFilter authHeaderForwardFilter() {
        return (exchange, chain) -> {
            String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (auth != null) {
                ServerHttpRequest mutated = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .build();
                ServerWebExchange mutatedExchange = exchange.mutate().request(mutated).build();
                return chain.filter(mutatedExchange);
            }
            return chain.filter(exchange);
        };
    }

    @Bean
    @Order(-3)
    public GlobalFilter timingFilter() {
        return (exchange, chain) -> {
            long start = System.currentTimeMillis();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long duration = System.currentTimeMillis() - start;
                exchange.getResponse().getHeaders().add("X-Response-Time", duration + "ms");
            }));
        };
    }
}
