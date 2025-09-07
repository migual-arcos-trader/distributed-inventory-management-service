package com.meli.distributed_inventory_management_service.application.dto.security;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}