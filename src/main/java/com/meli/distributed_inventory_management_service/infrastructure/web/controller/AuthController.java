package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.infrastructure.config.security.JwtUtil;
import com.meli.distributed_inventory_management_service.infrastructure.web.dto.AuthRequestDTO;
import com.meli.distributed_inventory_management_service.infrastructure.web.dto.AuthResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";
    private static final String PASSWORD = "password";
    private static final String BEARER = "Bearer";
    public static final long EXPIRES_IN = 3600L;

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request) {
        if (ADMIN.equalsIgnoreCase(request.username()) && PASSWORD.equals(request.password())) {
            String token = jwtUtil.generateToken(request.username(), List.of(ADMIN, USER));
            AuthResponseDTO response = new AuthResponseDTO(token, BEARER, EXPIRES_IN, request.username());
            return Mono.just(ResponseEntity.ok(response));
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);
            return Mono.just(ResponseEntity.ok(isValid));
        }
        return Mono.just(ResponseEntity.ok(false));
    }
}