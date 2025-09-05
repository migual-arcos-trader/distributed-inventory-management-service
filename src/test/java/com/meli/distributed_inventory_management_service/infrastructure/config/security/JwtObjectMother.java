package com.meli.distributed_inventory_management_service.infrastructure.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

public final class JwtObjectMother {

    private JwtObjectMother() {
        // Utility class
    }

    public static String createValidToken() {
        return createToken(SecurityTestConstants.TEST_USERNAME, List.of(SecurityTestConstants.ROLE_USER),
                new Date(System.currentTimeMillis() + SecurityTestConstants.TEST_JWT_EXPIRATION));
    }

    public static String createExpiredToken() {
        return createToken(SecurityTestConstants.TEST_USERNAME, List.of(SecurityTestConstants.ROLE_USER),
                new Date(System.currentTimeMillis() - 1000));
    }

    public static String createAdminToken() {
        return createToken(SecurityTestConstants.TEST_ADMIN_USERNAME,
                List.of(SecurityTestConstants.ROLE_ADMIN, SecurityTestConstants.ROLE_USER),
                new Date(System.currentTimeMillis() + SecurityTestConstants.TEST_JWT_EXPIRATION));
    }

    public static String createTokenWithCustomRoles(List<String> roles) {
        return createToken(SecurityTestConstants.TEST_USERNAME, roles,
                new Date(System.currentTimeMillis() + SecurityTestConstants.TEST_JWT_EXPIRATION));
    }

    private static String createToken(String username, List<String> roles, Date expiration) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityTestConstants.TEST_JWT_SECRET.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public static String createMalformedToken() {
        return SecurityTestConstants.TEST_INVALID_TOKEN;
    }

}
