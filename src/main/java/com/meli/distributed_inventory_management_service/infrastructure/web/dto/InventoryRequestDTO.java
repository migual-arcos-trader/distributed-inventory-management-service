package com.meli.distributed_inventory_management_service.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequestDTO(
        @NotBlank(message = "Product ID is required")
        String productId,

        @NotBlank(message = "Store ID is required")
        String storeId,

        @NotNull(message = "Current stock is required")
        @PositiveOrZero(message = "Current stock must be zero or positive")
        Integer currentStock,

        @NotNull(message = "Reserved stock is required")
        @PositiveOrZero(message = "Reserved stock must be zero or positive")
        Integer reservedStock,

        @PositiveOrZero(message = "Minimum stock level must be zero or positive")
        Integer minimumStockLevel,

        @PositiveOrZero(message = "Maximum stock level must be zero or positive")
        Integer maximumStockLevel
) {
}