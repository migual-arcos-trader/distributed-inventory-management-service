package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.usecase.EventUseCase;
import com.meli.distributed_inventory_management_service.application.port.EventRepositoryPort;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Component
@RequiredArgsConstructor
public class EventUseCaseImpl implements EventUseCase {

    private final EventRepositoryPort eventRepositoryPort;

    @Override
    public Mono<EventResponseDTO> getEvent(String eventId) {
        return eventRepositoryPort.findById(eventId)
                .map(this::toResponseDTO);
    }

    @Override
    public Flux<EventResponseDTO> getAllEvents() {
        return eventRepositoryPort.findAll()
                .map(this::toResponseDTO);
    }

    @Override
    public Flux<EventResponseDTO> getEventsByStatus(String status) {
        return eventRepositoryPort.findByStatus(status)
                .map(this::toResponseDTO);
    }

    @Override
    public Flux<EventResponseDTO> getEventsByCorrelationId(String correlationId) {
        return eventRepositoryPort.findByCorrelationId(correlationId)
                .map(this::toResponseDTO);
    }

    @Override
    public Mono<EventResponseDTO> compensateEvent(String eventId, String reason) {
        return eventRepositoryPort.findById(eventId)
                .flatMap(event -> {
                    if (!event.isCompensatable()) {
                        return Mono.error(new IllegalStateException("Event cannot be compensated: " + eventId));
                    }

                    return eventRepositoryPort.updateStatus(eventId, STATUS_COMPENSATED, reason)
                            .map(this::toResponseDTO);
                });
    }

    private EventResponseDTO toResponseDTO(InventoryUpdateEvent event) {
        return new EventResponseDTO(
                event.getEventId(),
                event.getProductId(),
                event.getStoreId(),
                event.getQuantity(),
                event.getUpdateType(),
                event.getSource(),
                event.getCorrelationId(),
                event.getTimestamp(),
                event.getStatus(),
                event.getErrorDetails()
        );
    }
}