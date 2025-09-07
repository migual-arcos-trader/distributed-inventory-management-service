package com.meli.distributed_inventory_management_service.infrastructure.config.security;

public final class SecurityTestConstants {

    // JWT Configuration
    public static final String TEST_JWT_SECRET = "testSecretKeyWithAtLeast256BitsLengthForSecurity123";
    public static final Long TEST_JWT_EXPIRATION = 3600000L;
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_ADMIN_USERNAME = "admin";
    public static final String TEST_PASSWORD = "password";
    public static final String TEST_INVALID_USERNAME = "invaliduser";
    public static final String TEST_INVALID_PASSWORD = "wrongpassword";
    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_PREFIX = "ROLE_";
    // Tokens
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TEST_VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjpbIlVTRVIiXSwiZXhwIjozMTYzMzc5OTk5OTl9.testSignature";
    public static final String TEST_INVALID_TOKEN = "invalid.token.signature";
    public static final String TEST_EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjpbIlVTRVIiXSwiZXhwIjoxNjAwMDAwMDAwfQ.expiredSignature";
    // Paths
    public static final String AUTH_LOGIN_PATH = "/api/auth/login";
    public static final String AUTH_VALIDATE_PATH = "/api/auth/validate";
    public static final String INVENTORY_PATH = "/api/inventory";
    public static final String HEALTH_PATH = "/actuator/health";
    public static final String SWAGGER_UI_PATH = "/swagger-ui.html";
    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_ALG = "alg";
    public static final String HEADER_ALG_VALUE = "HS256";
    public static final String HEADER_SUB = "sub";
    public static final String HEADER_ROLES = "roles";
    // Responses
    public static final int HTTP_OK = 200;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;

    private SecurityTestConstants() {
        // Utility class
    }

}