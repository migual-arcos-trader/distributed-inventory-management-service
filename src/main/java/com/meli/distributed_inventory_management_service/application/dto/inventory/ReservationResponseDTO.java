package com.meli.distributed_inventory_management_service.application.dto.inventory;

import java.time.LocalDateTime;

public record ReservationResponseDTO(
        String reservationId,
        String productId,
        String storeId,
        Integer quantity,
        String status,
        LocalDateTime reservedAt,
        LocalDateTime expiresAt,
        String correlationId
) {}