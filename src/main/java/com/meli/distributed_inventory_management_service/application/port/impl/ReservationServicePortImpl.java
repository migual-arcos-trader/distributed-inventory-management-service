package com.meli.distributed_inventory_management_service.application.port.impl;

import com.meli.distributed_inventory_management_service.application.port.ReservationRepositoryPort;
import com.meli.distributed_inventory_management_service.application.port.ReservationServicePort;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.RESERVATION_PREFIX;
import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.STATUS_RESERVED;

@Component
@RequiredArgsConstructor
public class ReservationServicePortImpl implements ReservationServicePort {

    private final InventoryService inventoryService;
    private final ReservationRepositoryPort reservationRepositoryPort;

    @Override
    public Mono<InventoryItem> reserveStock(String productId, String storeId, Integer quantity) {
        return inventoryService.reserveStock(productId, storeId, quantity);
    }

    @Override
    public Mono<InventoryItem> releaseReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Reservation not found: " + reservationId)))
                .flatMap(reservation ->
                        inventoryService.releaseReservedStock(
                                reservation.getProductId(),
                                reservation.getStoreId(),
                                reservation.getQuantity()
                        )
                );
    }

    @Override
    public Mono<InventoryItem> confirmReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Reservation not found: " + reservationId)))
                .flatMap(reservation ->
                        inventoryService.updateStockWithRetry(
                                reservation.getProductId(),
                                reservation.getStoreId(),
                                reservation.getQuantity(),
                                com.meli.distributed_inventory_management_service.domain.model.UpdateType.SALE
                        )
                );
    }

    @Override
    public Mono<Boolean> isReservationValid(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .map(reservation ->
                        reservation != null &&
                                !reservation.isExpired() &&
                                STATUS_RESERVED.equals(reservation.getStatus())
                )
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<String> generateReservationId() {
        return Mono.fromCallable(() -> RESERVATION_PREFIX + UUID.randomUUID());
    }
}