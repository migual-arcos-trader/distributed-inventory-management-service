package com.meli.distributed_inventory_management_service.application.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

public record InventoryRequestDTO(
        @NotBlank(message = VALIDATION_PRODUCT_ID_REQUIRED)
        String productId,

        @NotBlank(message = VALIDATION_STORE_ID_REQUIRED)
        String storeId,

        @NotNull(message = VALIDATION_STOCK_REQUIRED)
        @PositiveOrZero(message = "Current stock" + VALIDATION_STOCK_POSITIVE)
        Integer currentStock,

        @NotNull(message = "Reserved stock is required")
        @PositiveOrZero(message = "Reserved stock" + VALIDATION_STOCK_POSITIVE)
        Integer reservedStock,

        @PositiveOrZero(message = "Minimum stock level" + VALIDATION_STOCK_POSITIVE)
        Integer minimumStockLevel,

        @PositiveOrZero(message = "Maximum stock level" + VALIDATION_STOCK_POSITIVE)
        Integer maximumStockLevel
) {
}