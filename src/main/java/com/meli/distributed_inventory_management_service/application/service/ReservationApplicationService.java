package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.application.port.ReservationRepositoryPort;
import com.meli.distributed_inventory_management_service.application.port.ReservationServicePort;
import com.meli.distributed_inventory_management_service.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Service
@RequiredArgsConstructor
public class ReservationApplicationService {

    private final ReservationServicePort reservationServicePort;
    private final ReservationRepositoryPort reservationRepositoryPort;

    public Mono<ReservationResponseDTO> createReservation(ReservationRequestDTO request) {
        return reservationServicePort.generateReservationId()
                .flatMap(reservationId -> {
                    Reservation reservation = Reservation.builder()
                            .reservationId(reservationId)
                            .productId(request.productId())
                            .storeId(request.storeId())
                            .quantity(request.quantity())
                            .status(STATUS_PENDING)
                            .createdAt(LocalDateTime.now())
                            .expiresAt(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES))
                            .correlationId(request.correlationId())
                            .build();

                    return reservationRepositoryPort.save(reservation)
                            .then(reservationServicePort.reserveStock(request.productId(), request.storeId(), request.quantity()))
                            .then(updateReservationStatus(reservationId, STATUS_RESERVED))
                            .map(this::toResponseDTO);
                });
    }

    public Mono<ReservationResponseDTO> confirmReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .flatMap(reservation -> {
                    if (!reservation.canBeConfirmed()) {
                        return Mono.error(new IllegalStateException("Reservation cannot be confirmed"));
                    }

                    return reservationServicePort.confirmReservation(reservationId)
                            .then(updateReservationStatus(reservationId, STATUS_CONFIRMED))
                            .map(this::toResponseDTO);
                });
    }

    public Mono<ReservationResponseDTO> releaseReservation(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .flatMap(reservation -> {
                    if (!reservation.canBeReleased()) {
                        return Mono.error(new IllegalStateException("Reservation cannot be released"));
                    }

                    return reservationServicePort.releaseReservation(reservationId)
                            .then(updateReservationStatus(reservationId, STATUS_RELEASED))
                            .map(this::toResponseDTO);
                });
    }

    public Mono<ReservationResponseDTO> getReservationById(String reservationId) {
        return reservationRepositoryPort.findById(reservationId)
                .map(this::toResponseDTO);
    }

    public Flux<ReservationResponseDTO> getAllReservations() {
        return reservationRepositoryPort.findAll()
                .map(this::toResponseDTO);
    }

    public Flux<ReservationResponseDTO> getExpiredReservations() {
        return reservationRepositoryPort.findExpiredReservations()
                .map(this::toResponseDTO);
    }

    public Mono<Boolean> isReservationValid(String reservationId) {
        return reservationServicePort.isReservationValid(reservationId);
    }

    private Mono<Reservation> updateReservationStatus(String reservationId, String status) {
        return reservationRepositoryPort.updateStatus(reservationId, status);
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