package com.meli.distributed_inventory_management_service.infrastructure.web.dto;

import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockUpdateRequestDTO(
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Update type is required")
        UpdateType updateType
) {}