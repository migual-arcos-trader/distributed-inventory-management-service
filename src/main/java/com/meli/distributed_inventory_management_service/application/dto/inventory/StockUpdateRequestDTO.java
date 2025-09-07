package com.meli.distributed_inventory_management_service.application.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

public record StockUpdateRequestDTO(
        @NotBlank(message = VALIDATION_PRODUCT_ID_REQUIRED)
        String productId,

        @NotBlank(message = VALIDATION_STORE_ID_REQUIRED)
        String storeId,

        @NotNull(message = "Quantity is required")
        @Positive(message = VALIDATION_QUANTITY_POSITIVE)
        Integer quantity,

        @NotNull(message = "Update type is required")
        UpdateType updateType,

        String correlationId
) {}