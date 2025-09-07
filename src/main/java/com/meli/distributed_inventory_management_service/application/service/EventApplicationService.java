package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.usecase.EventUseCase;
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
public class EventApplicationService {

    private final EventUseCase eventUseCase;

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<EventResponseDTO> getEventById(String eventId) {
        return eventUseCase.getEvent(eventId);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Flux<EventResponseDTO> getAllEvents() {
        return eventUseCase.getAllEvents();
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Flux<EventResponseDTO> getEventsByStatus(String status) {
        return eventUseCase.getEventsByStatus(status);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Flux<EventResponseDTO> getEventsByCorrelationId(String correlationId) {
        return eventUseCase.getEventsByCorrelationId(correlationId);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<EventResponseDTO> compensateEvent(String eventId, String reason) {
        return eventUseCase.compensateEvent(eventId, reason);
    }
}