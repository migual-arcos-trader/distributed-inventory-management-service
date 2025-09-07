//package com.meli.distributed_inventory_management_service.infrastructure.config.security;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.server.WebFilter;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@DisplayName("SecurityConfig Integration Tests")
//class SecurityConfigIntegrationTest {
//
//    @Autowired
//    private SecurityConfig securityConfig;
//
//    @Test
//    @DisplayName("Should load security configuration")
//    void shouldLoadSecurityConfiguration() {
//        // Act
//        SecurityWebFilterChain result = securityConfig.securityWebFilterChain();
//
//        // Assert
//        assertNotNull(result);
//    }
//
//    @Test
//    @DisplayName("Should create JWT authentication filter")
//    void shouldCreateJwtAuthenticationFilter() {
//        // Act
//        WebFilter filter = securityConfig.jwtAuthenticationFilter();
//
//        // Assert
//        assertNotNull(filter);
//    }
//}