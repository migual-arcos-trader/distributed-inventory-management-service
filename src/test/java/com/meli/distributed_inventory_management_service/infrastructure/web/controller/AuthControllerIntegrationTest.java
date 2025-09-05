package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.infrastructure.config.security.JwtUtil;
import com.meli.distributed_inventory_management_service.infrastructure.web.dto.AuthRequestDTO;
import com.meli.distributed_inventory_management_service.infrastructure.config.security.SecurityTestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Should return token on successful login")
    void shouldReturnTokenOnSuccessfulLogin() {
        // Arrange
        AuthRequestDTO request = AuthObjectMother.createValidAuthRequest();
        when(jwtUtil.generateToken(any(), anyList())).thenReturn(SecurityTestConstants.TEST_VALID_TOKEN);
        when(jwtUtil.getExpiration()).thenReturn(SecurityTestConstants.TEST_JWT_EXPIRATION);

        // Act & Assert
        webTestClient.post()
                .uri(SecurityTestConstants.AUTH_LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AuthRequestDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo(SecurityTestConstants.TEST_VALID_TOKEN)
                .jsonPath("$.tokenType").isEqualTo("Bearer")
                .jsonPath("$.username").isEqualTo(SecurityTestConstants.TEST_ADMIN_USERNAME);
    }

    @Test
    @DisplayName("Should return unauthorized on invalid credentials")
    void shouldReturnUnauthorizedOnInvalidCredentials() {
        // Arrange
        AuthRequestDTO request = AuthObjectMother.createInvalidAuthRequest();

        // Act & Assert
        webTestClient.post()
                .uri(SecurityTestConstants.AUTH_LOGIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AuthRequestDTO.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        // Arrange
        when(jwtUtil.validateToken(SecurityTestConstants.TEST_VALID_TOKEN)).thenReturn(true);

        // Act & Assert
        webTestClient.post()
                .uri(SecurityTestConstants.AUTH_VALIDATE_PATH)
                .header(SecurityTestConstants.AUTHORIZATION_HEADER,
                        SecurityTestConstants.BEARER_PREFIX + SecurityTestConstants.TEST_VALID_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Should invalidate invalid token")
    void shouldInvalidateInvalidToken() {
        // Arrange
        when(jwtUtil.validateToken(SecurityTestConstants.TEST_INVALID_TOKEN)).thenReturn(false);

        // Act & Assert
        webTestClient.post()
                .uri(SecurityTestConstants.AUTH_VALIDATE_PATH)
                .header(SecurityTestConstants.AUTHORIZATION_HEADER,
                        SecurityTestConstants.BEARER_PREFIX + SecurityTestConstants.TEST_INVALID_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(false);
    }

    @Test
    @DisplayName("Should return false when no authorization header")
    void shouldReturnFalseWhenNoAuthorizationHeader() {
        // Act & Assert
        webTestClient.post()
                .uri(SecurityTestConstants.AUTH_VALIDATE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(false);
    }

}