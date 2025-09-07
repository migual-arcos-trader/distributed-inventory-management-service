package com.meli.distributed_inventory_management_service.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Value
@Builder
public class Reservation {
    String reservationId;
    String productId;
    String storeId;
    Integer quantity;
    String status;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    String correlationId;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean canBeConfirmed() {
        return STATUS_RESERVED.equals(status) && !isExpired();
    }

    public boolean canBeReleased() {
        return STATUS_RESERVED.equals(status) || STATUS_PENDING.equals(status);
    }

    public Reservation confirm() {
        return Reservation.builder()
                .reservationId(this.reservationId)
                .productId(this.productId)
                .storeId(this.storeId)
                .quantity(this.quantity)
                .status(STATUS_CONFIRMED)
                .createdAt(this.createdAt)
                .expiresAt(this.expiresAt)
                .correlationId(this.correlationId)
                .build();
    }

    public Reservation release() {
        return Reservation.builder()
                .reservationId(this.reservationId)
                .productId(this.productId)
                .storeId(this.storeId)
                .quantity(this.quantity)
                .status(STATUS_RELEASED)
                .createdAt(this.createdAt)
                .expiresAt(this.expiresAt)
                .correlationId(this.correlationId)
                .build();
    }

    public Reservation markAsExpired() {
        return Reservation.builder()
                .reservationId(this.reservationId)
                .productId(this.productId)
                .storeId(this.storeId)
                .quantity(this.quantity)
                .status(STATUS_EXPIRED)
                .createdAt(this.createdAt)
                .expiresAt(this.expiresAt)
                .correlationId(this.correlationId)
                .build();
    }
}