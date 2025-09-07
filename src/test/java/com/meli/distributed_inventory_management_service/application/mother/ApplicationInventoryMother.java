package com.meli.distributed_inventory_management_service.application.mother;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.domain.model.EventStatus;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import com.meli.distributed_inventory_management_service.domain.model.Reservation;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;

import java.time.LocalDateTime;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

public class ApplicationInventoryMother {

    private ApplicationInventoryMother() {
    }

    // Reservation Object Mother
    public static Reservation.ReservationBuilder basicReservation() {
        return Reservation.builder()
                .reservationId(ApplicationTestConstants.RESERVATION_ID)
                .productId(ApplicationTestConstants.PRODUCT_ID)
                .storeId(ApplicationTestConstants.STORE_ID)
                .quantity(ApplicationTestConstants.QUANTITY)
                .status(STATUS_PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES))
                .correlationId(ApplicationTestConstants.CORRELATION_ID);
    }

    public static Reservation createReservationWithStatus(String status) {
        return basicReservation()
                .status(status)
                .build();
    }

    public static Reservation createExpiredReservation() {
        return basicReservation()
                .status(STATUS_RESERVED)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();
    }

    public static Reservation createValidReservation() {
        return basicReservation()
                .status(STATUS_RESERVED)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
    }

    // Event Object Mother
    public static InventoryUpdateEvent.InventoryUpdateEventBuilder basicEvent() {
        return InventoryUpdateEvent.builder()
                .eventId(ApplicationTestConstants.EVENT_ID)
                .productId(ApplicationTestConstants.PRODUCT_ID)
                .storeId(ApplicationTestConstants.STORE_ID)
                .quantity(ApplicationTestConstants.QUANTITY)
                .updateType(UpdateType.PURCHASE)
                .source(ApplicationTestConstants.EVENT_SOURCE)
                .correlationId(ApplicationTestConstants.CORRELATION_ID)
                .timestamp(LocalDateTime.now())
                .status(EventStatus.PENDING)
                .errorDetails(null);
    }

    public static InventoryUpdateEvent createEventWithStatus(EventStatus status) {
        return basicEvent()
                .status(status)
                .build();
    }

    public static InventoryUpdateEvent createCompensatableEvent() {
        return basicEvent()
                .updateType(UpdateType.SALE)
                .status(EventStatus.PROCESSED)
                .build();
    }

    public static InventoryUpdateEvent createNonCompensatableEvent() {
        return basicEvent()
                .updateType(UpdateType.ADJUSTMENT)
                .status(EventStatus.PROCESSED)
                .build();
    }
}