package com.meli.distributed_inventory_management_service.application.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

public record ReservationConfirmationDTO(
        @NotBlank(message = VALIDATION_RESERVATION_ID_REQUIRED)
        String reservationId,

        String correlationId
) {}