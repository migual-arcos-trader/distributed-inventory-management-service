package com.meli.distributed_inventory_management_service.infrastructure.web.dto;

public record AuthResponseDTO(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String username
) {
    public AuthResponseDTO(String token, Long expiresIn, String username) {
        this(token, "Bearer", expiresIn, username);
    }
}
