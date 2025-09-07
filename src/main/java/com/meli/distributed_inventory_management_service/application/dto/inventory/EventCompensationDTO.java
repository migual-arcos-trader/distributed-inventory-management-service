package com.meli.distributed_inventory_management_service.application.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

public record EventCompensationDTO(
        @NotBlank(message = VALIDATION_EVENT_ID_REQUIRED)
        String eventId,

        String compensationReason,
        String correlationId
) {}