package com.meli.distributed_inventory_management_service.application.port.impl;

import com.meli.distributed_inventory_management_service.application.port.EventRepositoryPort;
import com.meli.distributed_inventory_management_service.domain.model.EventStatus;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventRepositoryPortImpl implements EventRepositoryPort {

    private final Map<String, InventoryUpdateEvent> eventStore = new ConcurrentHashMap<>();

    @Override
    public Mono<InventoryUpdateEvent> save(InventoryUpdateEvent event) {
        eventStore.put(event.getEventId(), event);
        return Mono.just(event);
    }

    @Override
    public Mono<InventoryUpdateEvent> findById(String eventId) {
        return Mono.justOrEmpty(eventStore.get(eventId));
    }

    @Override
    public Flux<InventoryUpdateEvent> findAll() {
        return Flux.fromIterable(eventStore.values());
    }

    @Override
    public Flux<InventoryUpdateEvent> findByStatus(String status) {
        return Flux.fromIterable(eventStore.values())
                .filter(event -> event.getStatus().name().equals(status));
    }

    @Override
    public Mono<InventoryUpdateEvent> updateStatus(String eventId, String status, String errorDetails) {
        return findById(eventId)
                .map(existingEvent -> {
                    EventStatus newStatus = EventStatus.valueOf(status);
                    InventoryUpdateEvent updatedEvent;

                    if (errorDetails != null) {
                        updatedEvent = existingEvent.withError(errorDetails);
                    } else {
                        updatedEvent = existingEvent.withStatus(newStatus);
                    }

                    eventStore.put(eventId, updatedEvent);
                    return updatedEvent;
                });
    }

    @Override
    public Flux<InventoryUpdateEvent> findByCorrelationId(String correlationId) {
        return Flux.fromIterable(eventStore.values())
                .filter(event -> correlationId != null && correlationId.equals(event.getCorrelationId()));
    }
}