package com.aryan.gatewayservice.filter;

import com.aryan.gatewayservice.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter {

    private final JWTUtil jwtUtil;

    @Value("${ecom.token}")
    private String systemToken;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 🔐 Aucun header → rejet
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // ✅ Appel interne → accepté
        if (token.equals(systemToken)) {
            return chain.filter(exchange);
        }

        // ✅ Sinon : appel utilisateur → vérifier JWT
        try {
            String username = jwtUtil.extractUsername(token);
            exchange.mutate()
                    .request(
                            exchange.getRequest().mutate()
                                    .header("X-User", username)
                                    .build()
                    )
                    .build();
            log.info("Valid JWT for user: {}", username);
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("Invalid JWT", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
