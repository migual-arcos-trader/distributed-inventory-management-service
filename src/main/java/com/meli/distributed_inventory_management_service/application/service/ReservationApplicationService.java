package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.application.usecase.ReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReservationApplicationService {

    private final ReservationUseCase reservationUseCase;

    public Mono<ReservationResponseDTO> createReservation(ReservationRequestDTO request) {
        return reservationUseCase.createReservation(
                request.productId(),
                request.storeId(),
                request.quantity(),
                request.correlationId()
        );
    }

    public Mono<ReservationResponseDTO> confirmReservation(String reservationId) {
        return reservationUseCase.confirmReservation(reservationId);
    }

    public Mono<ReservationResponseDTO> releaseReservation(String reservationId) {
        return reservationUseCase.releaseReservation(reservationId);
    }

    public Mono<ReservationResponseDTO> getReservationById(String reservationId) {
        return reservationUseCase.getReservation(reservationId);
    }

    public Flux<ReservationResponseDTO> getAllReservations() {
        return reservationUseCase.getAllReservations();
    }

    public Mono<Boolean> isReservationValid(String reservationId) {
        return reservationUseCase.validateReservation(reservationId);
    }
}