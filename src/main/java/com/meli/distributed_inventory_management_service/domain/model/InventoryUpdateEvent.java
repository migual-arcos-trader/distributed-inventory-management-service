package com.meli.distributed_inventory_management_service.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder

public class InventoryUpdateEvent {

    String eventId;
    String productId;
    String storeId;
    Integer quantity;
    UpdateType updateType;
    String source;
    String correlationId;
    LocalDateTime timestamp;
    EventStatus status;
    String errorDetails;

    public boolean isCompensatable() {
        return updateType == UpdateType.SALE || updateType == UpdateType.PURCHASE;
    }

    public InventoryUpdateEvent withStatus(EventStatus newStatus) {
        return InventoryUpdateEvent.builder()
                .eventId(this.eventId)
                .productId(this.productId)
                .storeId(this.storeId)
                .quantity(this.quantity)
                .updateType(this.updateType)
                .source(this.source)
                .correlationId(this.correlationId)
                .timestamp(this.timestamp)
                .status(newStatus)
                .errorDetails(this.errorDetails)
                .build();
    }

    public InventoryUpdateEvent withError(String error) {
        return InventoryUpdateEvent.builder()
                .eventId(this.eventId)
                .productId(this.productId)
                .storeId(this.storeId)
                .quantity(this.quantity)
                .updateType(this.updateType)
                .source(this.source)
                .correlationId(this.correlationId)
                .timestamp(this.timestamp)
                .status(EventStatus.FAILED)
                .errorDetails(error)
                .build();
    }

}
