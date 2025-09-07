package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.security.AuthRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.security.AuthResponseDTO;
import com.meli.distributed_inventory_management_service.infrastructure.config.security.JwtUtil;
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
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final int BEGIN_INDEX = 7;
    private static final int MILLISECONDS_TO_SECOND = 1000;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request) {
        if (ADMIN.toLowerCase().equals(request.username()) && PASSWORD.equals(request.password())) {
            String token = jwtUtil.generateToken(request.username(), List.of(ADMIN, USER));
            AuthResponseDTO response = new AuthResponseDTO(token, jwtUtil.getExpiration() / MILLISECONDS_TO_SECOND, request.username());
            return Mono.just(ResponseEntity.ok(response));
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validateToken(@RequestHeader(value = AUTHORIZATION, required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(BEGIN_INDEX);
            boolean isValid = jwtUtil.validateToken(token);
            return Mono.just(ResponseEntity.ok(isValid));
        }
        return Mono.just(ResponseEntity.ok(false));
    }

}
