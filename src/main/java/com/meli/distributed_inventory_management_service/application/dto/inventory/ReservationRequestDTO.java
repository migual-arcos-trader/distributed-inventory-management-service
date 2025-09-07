package com.meli.distributed_inventory_management_service.application.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

public record ReservationRequestDTO(
        @NotBlank(message = VALIDATION_PRODUCT_ID_REQUIRED)
        String productId,

        @NotBlank(message = VALIDATION_STORE_ID_REQUIRED)
        String storeId,

        @NotNull(message = VALIDATION_QUANTITY_REQUIRED)
        @Positive(message = VALIDATION_QUANTITY_POSITIVE)
        Integer quantity,

        String reservationId,
        String correlationId
) {}