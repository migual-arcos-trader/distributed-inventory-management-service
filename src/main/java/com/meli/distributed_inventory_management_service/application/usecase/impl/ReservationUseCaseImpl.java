package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.application.usecase.ReservationUseCase;
import com.meli.distributed_inventory_management_service.application.port.ReservationRepositoryPort;
import com.meli.distributed_inventory_management_service.application.port.ReservationServicePort;
import com.meli.distributed_inventory_management_service.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Component
@RequiredArgsConstructor
public class ReservationUseCaseImpl implements ReservationUseCase {

    private final ReservationServicePort reservationServicePort;
    private final ReservationRepositoryPort reservationRepositoryPort;

    @Override
    public Mono<ReservationResponseDTO> createReservation(String productId, String storeId, Integer quantity, String correlationId) {
        return reservationServicePort.generateReservationId()
                .flatMap(reservationId -> {
                    Reservation reservation = Reservation.builder()
                            .reservationId(reservationId)
                            .productId(productId)
                            .storeId(storeId)
                            .quantity(quantity)
                            .status(STATUS_PENDING)
                            .createdAt(LocalDateTime.now())
                            .expiresAt(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES))
                            .correlationId(correlationId)
                            .build();

                    return reservationRepositoryPort.save(reservation)
                            .then(reservationServicePort.reserveStock(productId, storeId, quantity))
                            .then(reservationRepositoryPort.updateStatus(reservationId, STATUS_RESERVED))
                            .flatMap(updatedReservation -> reservationRepositoryPort.findById(reservationId))
                            .map(this::toResponseDTO);
                });
    }

    @Override
    public Mono<ReservationResponseDTO> confirmReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .flatMap(reservation -> {
                    if (!reservation.canBeConfirmed()) {
                        return Mono.error(new IllegalStateException("Cannot confirm reservation: " + reservationId));
                    }

                    return reservationServicePort.confirmReservation(reservationId)
                            .then(reservationRepositoryPort.updateStatus(reservationId, STATUS_CONFIRMED))
                            .flatMap(updatedReservation -> reservationRepositoryPort.findById(reservationId))
                            .map(this::toResponseDTO);
                });
    }

    @Override
    public Mono<ReservationResponseDTO> releaseReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Reservation not found: " + reservationId)))
                .flatMap(reservation -> {
                    if (!reservation.canBeReleased()) {
                        return Mono.error(new IllegalStateException("Cannot release reservation: " + reservationId));
                    }

                    return reservationServicePort.releaseReservation(reservationId)
                            .then(reservationRepositoryPort.updateStatus(reservationId, STATUS_RELEASED))
                            .then(reservationRepositoryPort.findById(reservationId))
                            .switchIfEmpty(Mono.error(new IllegalStateException("Reservation not found after update: " + reservationId)))
                            .map(this::toResponseDTO);
                });
    }

    @Override
    public Mono<ReservationResponseDTO> getReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .map(this::toResponseDTO);
    }

    @Override
    public Flux<ReservationResponseDTO> getAllReservations() {
        return reservationRepositoryPort.findAll()
                .map(this::toResponseDTO);
    }

    @Override
    public Mono<Boolean> validateReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .map(reservation -> reservation.canBeConfirmed())
                .defaultIfEmpty(false);
    }

    private ReservationResponseDTO toResponseDTO(Reservation reservation) {
        return new ReservationResponseDTO(
                reservation.getReservationId(),
                reservation.getProductId(),
                reservation.getStoreId(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getExpiresAt(),
                reservation.getCorrelationId()
        );
    }
}