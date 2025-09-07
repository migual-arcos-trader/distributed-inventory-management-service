package com.meli.distributed_inventory_management_service.infrastructure.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/api/auth/**",
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/actuator/health",
            "/webjars/**"
    };

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return token -> {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtUtil.getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                Instant issuedAt = Instant.ofEpochSecond(claims.get("iat", Integer.class));
                Instant expiresAt = Instant.ofEpochSecond(claims.get("exp", Integer.class));

                Jwt jwt = Jwt.withTokenValue(token)
                        .subject(claims.getSubject())
                        .header("alg", "HS256")
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .claims(c -> {
                            claims.forEach((key, value) -> {
                                if (!"iat".equals(key) && !"exp".equals(key)) {
                                    c.put(key, value);
                                }
                            });
                        })
                        .build();

                return Mono.just(jwt);
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Token validation failed: " + e.getMessage(), e));
            }
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public JwtAuthConverter jwtAuthConverter() {
        return new JwtAuthConverter();
    }
}