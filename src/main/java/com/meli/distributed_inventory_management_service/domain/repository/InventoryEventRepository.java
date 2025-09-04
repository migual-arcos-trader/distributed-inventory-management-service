package com.meli.distributed_inventory_management_service.domain.repository;


import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryEventRepository {

    Mono<InventoryUpdateEvent> save(InventoryUpdateEvent event);

    Flux<InventoryUpdateEvent> findPendingEvents();

    Flux<InventoryUpdateEvent> findByCorrelationId(String correlationId);

    Mono<InventoryUpdateEvent> updateStatus(String eventId, String status);

    Mono<InventoryUpdateEvent> findById(String eventId);

}