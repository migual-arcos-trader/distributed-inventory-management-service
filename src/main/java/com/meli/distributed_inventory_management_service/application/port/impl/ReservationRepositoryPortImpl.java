package com.meli.distributed_inventory_management_service.application.port.impl;

import com.meli.distributed_inventory_management_service.application.port.ReservationRepositoryPort;
import com.meli.distributed_inventory_management_service.domain.model.Reservation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Component
public class ReservationRepositoryPortImpl implements ReservationRepositoryPort {

    private final Map<String, Reservation> reservationStore = new ConcurrentHashMap<>();

    @Override
    public Mono<Reservation> save(Reservation reservation) {
        reservationStore.put(reservation.getReservationId(), reservation);
        return Mono.just(reservation);
    }

    @Override
    public Mono<Reservation> findById(String reservationId) {
        return Mono.justOrEmpty(reservationStore.get(reservationId));
    }

    @Override
    public Flux<Reservation> findAll() {
        return Flux.fromIterable(reservationStore.values());
    }

    @Override
    public Flux<Reservation> findByStatus(String status) {
        return Flux.fromIterable(reservationStore.values())
                .filter(reservation -> status.equals(reservation.getStatus()));
    }

    @Override
    public Flux<Reservation> findByProductAndStore(String productId, String storeId) {
        return Flux.fromIterable(reservationStore.values())
                .filter(reservation -> productId.equals(reservation.getProductId())
                        && storeId.equals(reservation.getStoreId()));
    }

    @Override
    public Mono<Reservation> updateStatus(String reservationId, String status) {
        return findById(reservationId)
                .map(reservation -> {
                    Reservation updatedReservation = Reservation.builder()
                            .reservationId(reservation.getReservationId())
                            .productId(reservation.getProductId())
                            .storeId(reservation.getStoreId())
                            .quantity(reservation.getQuantity())
                            .status(status)
                            .createdAt(reservation.getCreatedAt())
                            .expiresAt(reservation.getExpiresAt())
                            .correlationId(reservation.getCorrelationId())
                            .build();
                    reservationStore.put(reservationId, updatedReservation);
                    return updatedReservation;
                });
    }

    @Override
    public Flux<Reservation> findExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        return Flux.fromIterable(reservationStore.values())
                .filter(reservation -> now.isAfter(reservation.getExpiresAt())
                        && (STATUS_PENDING.equals(reservation.getStatus())
                        || STATUS_RESERVED.equals(reservation.getStatus())));
    }

    @Override
    public Mono<Boolean> existsById(String reservationId) {
        return Mono.just(reservationStore.containsKey(reservationId));
    }
}