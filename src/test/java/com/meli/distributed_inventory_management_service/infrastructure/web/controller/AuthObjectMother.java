package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.infrastructure.web.dto.AuthRequestDTO;
import com.meli.distributed_inventory_management_service.infrastructure.config.security.SecurityTestConstants;

public final class AuthObjectMother {

    private AuthObjectMother() {
        // Utility class
    }

    public static AuthRequestDTO createValidAuthRequest() {
        return new AuthRequestDTO(SecurityTestConstants.TEST_ADMIN_USERNAME, SecurityTestConstants.TEST_PASSWORD);
    }

    public static AuthRequestDTO createInvalidAuthRequest() {
        return new AuthRequestDTO(SecurityTestConstants.TEST_INVALID_USERNAME, SecurityTestConstants.TEST_INVALID_PASSWORD);
    }

    public static AuthRequestDTO createAuthRequestWithNullUsername() {
        return new AuthRequestDTO(null, SecurityTestConstants.TEST_PASSWORD);
    }

    public static AuthRequestDTO createAuthRequestWithNullPassword() {
        return new AuthRequestDTO(SecurityTestConstants.TEST_ADMIN_USERNAME, null);
    }

    public static AuthRequestDTO createAuthRequestWithEmptyUsername() {
        return new AuthRequestDTO("", SecurityTestConstants.TEST_PASSWORD);
    }

    public static AuthRequestDTO createAuthRequestWithEmptyPassword() {
        return new AuthRequestDTO(SecurityTestConstants.TEST_ADMIN_USERNAME, "");
    }
}