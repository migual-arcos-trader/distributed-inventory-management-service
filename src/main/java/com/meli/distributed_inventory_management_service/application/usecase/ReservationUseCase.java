package com.meli.distributed_inventory_management_service.application.usecase;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationUseCase {
    Mono<ReservationResponseDTO> createReservation(String productId, String storeId, Integer quantity, String correlationId);

    Mono<ReservationResponseDTO> confirmReservation(String reservationId);

    Mono<ReservationResponseDTO> releaseReservation(String reservationId);

    Mono<ReservationResponseDTO> getReservation(String reservationId);

    Flux<ReservationResponseDTO> getAllReservations();

    Mono<Boolean> validateReservation(String reservationId);
}