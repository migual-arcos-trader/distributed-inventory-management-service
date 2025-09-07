package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.usecase.EventUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EventApplicationService {

    private final EventUseCase eventUseCase;

    public Mono<EventResponseDTO> getEventById(String eventId) {
        return eventUseCase.getEvent(eventId);
    }

    public Flux<EventResponseDTO> getAllEvents() {
        return eventUseCase.getAllEvents();
    }

    public Flux<EventResponseDTO> getEventsByStatus(String status) {
        return eventUseCase.getEventsByStatus(status);
    }

    public Flux<EventResponseDTO> getEventsByCorrelationId(String correlationId) {
        return eventUseCase.getEventsByCorrelationId(correlationId);
    }

    public Mono<EventResponseDTO> compensateEvent(String eventId, String reason) {
        return eventUseCase.compensateEvent(eventId, reason);
    }
}