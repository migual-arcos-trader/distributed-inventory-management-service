package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.security.AuthRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.security.AuthResponseDTO;
import com.meli.distributed_inventory_management_service.infrastructure.config.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "APIs for user authentication and token management")
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
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public Mono<ResponseEntity<AuthResponseDTO>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthRequestDTO.class)))
            @Valid @RequestBody AuthRequestDTO request) {
        if (ADMIN.toLowerCase().equals(request.username()) && PASSWORD.equals(request.password())) {
            String token = jwtUtil.generateToken(request.username(), List.of(ADMIN, USER));
            AuthResponseDTO response = new AuthResponseDTO(token, jwtUtil.getExpiration() / MILLISECONDS_TO_SECOND, request.username());
            return Mono.just(ResponseEntity.ok(response));
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Valida si el JWT token es válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public Mono<ResponseEntity<Boolean>> validateToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JWT token to validate", required = true,
                    content = @Content(mediaType = "application/json"))
            @RequestHeader(value = AUTHORIZATION, required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(BEGIN_INDEX);
            boolean isValid = jwtUtil.validateToken(token);
            return Mono.just(ResponseEntity.ok(isValid));
        }
        return Mono.just(ResponseEntity.ok(false));
    }

}
