package com.meli.distributed_inventory_management_service.application.dto.inventory;

import com.meli.distributed_inventory_management_service.domain.model.EventStatus;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;

import java.time.LocalDateTime;

public record EventResponseDTO(
        String eventId,
        String productId,
        String storeId,
        Integer quantity,
        UpdateType updateType,
        String source,
        String correlationId,
        LocalDateTime timestamp,
        EventStatus status,
        String errorDetails
) {
}