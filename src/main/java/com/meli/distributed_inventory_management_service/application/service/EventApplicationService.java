package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.port.EventRepositoryPort;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Service
@RequiredArgsConstructor
public class EventApplicationService {

    private final EventRepositoryPort eventRepositoryPort;

    public Mono<EventResponseDTO> getEventById(String eventId) {
        return eventRepositoryPort.findById(eventId)
                .map(this::toResponseDTO);
    }

    public Flux<EventResponseDTO> getAllEvents() {
        return eventRepositoryPort.findAll()
                .map(this::toResponseDTO);
    }

    public Flux<EventResponseDTO> getEventsByStatus(String status) {
        return eventRepositoryPort.findByStatus(status)
                .map(this::toResponseDTO);
    }

    public Flux<EventResponseDTO> getEventsByCorrelationId(String correlationId) {
        return eventRepositoryPort.findByCorrelationId(correlationId)
                .map(this::toResponseDTO);
    }

    public Mono<EventResponseDTO> compensateEvent(String eventId, String compensationReason) {
        return eventRepositoryPort.findById(eventId)
                .flatMap(event -> {
                    if (!event.isCompensatable()) {
                        return Mono.error(new IllegalStateException("Event cannot be compensated"));
                    }

                    return eventRepositoryPort.updateStatus(eventId, STATUS_COMPENSATED, compensationReason)
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