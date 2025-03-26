package com.aryan.gatewayservice.filter;

import com.aryan.gatewayservice.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter {

    @Value("${ecom.token}")
    private String systemToken;

    private final JWTUtil jwtUtil; // tu peux mocker ça si tu veux juste filtrer le token

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Autorise tout ce qui est public (ex: /authenticate, /sign-up)
        String path = request.getURI().getPath();
        if (path.contains("/authenticate") || path.contains("/sign-up")) {
            return chain.filter(exchange);
        }

        // Vérifie si l'en-tête Authorization est présent
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null) {
            log.warn("Missing Authorization header");
            return unauthorized(exchange);
        }

        // Vérifie si c’est un token interne
        if (authHeader.equals("Bearer " + systemToken)) {
            return chain.filter(exchange); // OK pour communication interne
        }

        // Sinon, c’est un JWT normal => valide-le (à toi de décider comment)
        if (authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(jwt);
                log.info("Valid JWT for user: {}", username);
                return chain.filter(exchange);
            } catch (Exception e) {
                log.error("Invalid JWT", e);
                return unauthorized(exchange);
            }
        }

        return unauthorized(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
