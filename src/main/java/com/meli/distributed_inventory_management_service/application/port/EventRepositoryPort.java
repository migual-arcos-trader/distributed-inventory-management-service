package com.meli.distributed_inventory_management_service.application.port;

import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepositoryPort {
    Mono<InventoryUpdateEvent> save(InventoryUpdateEvent event);

    Mono<InventoryUpdateEvent> findById(String eventId);

    Flux<InventoryUpdateEvent> findAll();

    Flux<InventoryUpdateEvent> findByStatus(String status);

    Mono<InventoryUpdateEvent> updateStatus(String eventId, String status, String errorDetails);

    Flux<InventoryUpdateEvent> findByCorrelationId(String correlationId);
}