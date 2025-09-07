package com.meli.distributed_inventory_management_service.application.port;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryRepositoryPort {

    Mono<InventoryItem> save(InventoryItem inventoryItem);

    Mono<InventoryItem> findById(String id);

    Mono<InventoryItem> findByProductIdAndStoreId(String productId, String storeId);

    Flux<InventoryItem> findAll();

    Flux<InventoryItem> findByStoreId(String storeId);

    Flux<InventoryItem> findByProductId(String productId);

    Mono<Boolean> deleteById(String id);

    Mono<Boolean> existsByProductIdAndStoreId(String productId, String storeId);

}
