package com.meli.distributed_inventory_management_service.application.port;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import reactor.core.publisher.Mono;

public interface ReservationServicePort {
    Mono<InventoryItem> reserveStock(String productId, String storeId, Integer quantity);

    Mono<InventoryItem> releaseReservation(String reservationId);

    Mono<InventoryItem> confirmReservation(String reservationId);

    Mono<Boolean> isReservationValid(String reservationId);

    Mono<String> generateReservationId();
}