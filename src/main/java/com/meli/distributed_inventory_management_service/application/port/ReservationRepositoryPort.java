package com.meli.distributed_inventory_management_service.application.port;

import com.meli.distributed_inventory_management_service.domain.model.Reservation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationRepositoryPort {
    Mono<Reservation> save(Reservation reservation);

    Mono<Reservation> findById(String reservationId);

    Flux<Reservation> findAll();

    Flux<Reservation> findByStatus(String status);

    Flux<Reservation> findByProductAndStore(String productId, String storeId);

    Mono<Reservation> updateStatus(String reservationId, String status);

    Flux<Reservation> findExpiredReservations();

    Mono<Boolean> existsById(String reservationId);
}