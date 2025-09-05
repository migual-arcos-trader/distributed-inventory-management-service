package com.meli.distributed_inventory_management_service.infrastructure.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.meli.distributed_inventory_management_service.infrastructure.config.security.SecurityTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthConverter Unit Tests")
class JwtAuthConverterTest {


    @InjectMocks
    private JwtAuthConverter jwtAuthConverter;

    private Jwt validJwt;
    private Jwt adminJwt;
    private Jwt jwtWithoutRoles;

    @BeforeEach
    void setUp() {
        validJwt = Jwt.withTokenValue(JwtObjectMother.createValidToken())
                .header(HEADER_ALG, HEADER_ALG_VALUE)
                .claim(HEADER_SUB, SecurityTestConstants.TEST_USERNAME)
                .claim(HEADER_ROLES, List.of(SecurityTestConstants.ROLE_USER))
                .build();

        adminJwt = Jwt.withTokenValue(JwtObjectMother.createAdminToken())
                .header(HEADER_ALG, HEADER_ALG_VALUE)
                .claim(HEADER_SUB, SecurityTestConstants.TEST_ADMIN_USERNAME)
                .claim(HEADER_ROLES, List.of(SecurityTestConstants.ROLE_ADMIN, SecurityTestConstants.ROLE_USER))
                .build();

        jwtWithoutRoles = Jwt.withTokenValue("token")
                .header(HEADER_ALG, HEADER_ALG_VALUE)
                .claim(HEADER_SUB, SecurityTestConstants.TEST_USERNAME)
                .build();
    }

    @Test
    @DisplayName("Should convert valid JWT to AuthenticationToken")
    void shouldConvertValidJwtToAuthenticationToken() {
        // Act
        Mono<AbstractAuthenticationToken> result = jwtAuthConverter.convert(validJwt);

        // Assert
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertNotNull(authentication);
                    assertEquals(SecurityTestConstants.TEST_USERNAME, authentication.getName());
                    assertTrue(authentication.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals(SecurityTestConstants.ROLE_PREFIX + SecurityTestConstants.ROLE_USER)));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle JWT with multiple roles")
    void shouldHandleJwtWithMultipleRoles() {
        // Act
        Mono<AbstractAuthenticationToken> result = jwtAuthConverter.convert(adminJwt);

        // Assert
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertEquals(2, authentication.getAuthorities().size());
                    assertTrue(authentication.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals(SecurityTestConstants.ROLE_PREFIX + SecurityTestConstants.ROLE_ADMIN)));
                    assertTrue(authentication.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals(SecurityTestConstants.ROLE_PREFIX + SecurityTestConstants.ROLE_USER)));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should assign default role when no roles present")
    void shouldAssignDefaultRoleWhenNoRolesPresent() {
        // Act
        Mono<AbstractAuthenticationToken> result = jwtAuthConverter.convert(jwtWithoutRoles);

        // Assert
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertTrue(authentication.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals(SecurityTestConstants.ROLE_PREFIX + SecurityTestConstants.ROLE_USER)));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle null JWT")
    void shouldHandleNullJwt() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> jwtAuthConverter.convert(null));
    }
}