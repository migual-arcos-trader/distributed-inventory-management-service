package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.application.usecase.ReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Service
@RequiredArgsConstructor
public class ReservationApplicationService {

    private final ReservationUseCase reservationUseCase;

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<ReservationResponseDTO> createReservation(ReservationRequestDTO request) {
        return reservationUseCase.createReservation(
                request.productId(),
                request.storeId(),
                request.quantity(),
                request.correlationId()
        );
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<ReservationResponseDTO> confirmReservation(String reservationId) {
        return reservationUseCase.confirmReservation(reservationId);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<ReservationResponseDTO> releaseReservation(String reservationId) {
        return reservationUseCase.releaseReservation(reservationId);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<ReservationResponseDTO> getReservationById(String reservationId) {
        return reservationUseCase.getReservation(reservationId);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Flux<ReservationResponseDTO> getAllReservations() {
        return reservationUseCase.getAllReservations();
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<Boolean> isReservationValid(String reservationId) {
        return reservationUseCase.validateReservation(reservationId);
    }
}