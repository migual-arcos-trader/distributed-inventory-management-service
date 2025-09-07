package com.meli.distributed_inventory_management_service.application.usecase;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventUseCase {
    Mono<EventResponseDTO> getEvent(String eventId);

    Flux<EventResponseDTO> getAllEvents();

    Flux<EventResponseDTO> getEventsByStatus(String status);

    Mono<EventResponseDTO> compensateEvent(String eventId, String reason);
}