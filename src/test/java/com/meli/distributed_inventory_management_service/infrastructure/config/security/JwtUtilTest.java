package com.meli.distributed_inventory_management_service.infrastructure.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", SecurityTestConstants.TEST_JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", SecurityTestConstants.TEST_JWT_EXPIRATION);
    }

    @Test
    @DisplayName("Should generate valid token")
    void shouldGenerateValidToken() {
        // Arrange
        String username = SecurityTestConstants.TEST_USERNAME;
        List<String> roles = List.of(SecurityTestConstants.ROLE_USER);

        // Act
        String token = jwtUtil.generateToken(username, roles);

        // Assert
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        // Arrange
        String validToken = JwtObjectMother.createValidToken();

        // Act
        boolean isValid = jwtUtil.validateToken(validToken);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate expired token")
    void shouldInvalidateExpiredToken() {
        // Arrange
        String expiredToken = JwtObjectMother.createExpiredToken();

        // Act
        boolean isValid = jwtUtil.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should invalidate malformed token")
    void shouldInvalidateMalformedToken() {
        // Arrange
        String malformedToken = SecurityTestConstants.TEST_INVALID_TOKEN;

        // Act
        boolean isValid = jwtUtil.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void shouldExtractUsernameFromValidToken() {
        // Arrange
        String validToken = JwtObjectMother.createValidToken();

        // Act
        String username = jwtUtil.getUsernameFromToken(validToken);

        // Assert
        assertEquals(SecurityTestConstants.TEST_USERNAME, username);
    }

    @Test
    @DisplayName("Should return null username from invalid token")
    void shouldReturnNullUsernameFromInvalidToken() {
        // Arrange
        String invalidToken = SecurityTestConstants.TEST_INVALID_TOKEN;

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtil.getUsernameFromToken(invalidToken));
    }

    @Test
    @DisplayName("Should handle null token")
    void shouldHandleNullToken() {
        // Act
        boolean isValid = jwtUtil.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle empty token")
    void shouldHandleEmptyToken() {
        // Act
        boolean isValid = jwtUtil.validateToken("");

        // Assert
        assertFalse(isValid);
    }
}