package com.meli.distributed_inventory_management_service.application.dto.inventory;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InventoryResponseDTO(
        String id,
        String productId,
        String storeId,
        Integer currentStock,
        Integer reservedStock,
        Integer minimumStockLevel,
        Integer maximumStockLevel,
        LocalDateTime lastUpdated,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}