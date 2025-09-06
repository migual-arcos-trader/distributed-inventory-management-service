package com.meli.distributed_inventory_management_service.domain.repository;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryRepository {

    Flux<InventoryItem> findAll();

    Mono<InventoryItem> findById(String id);

    Mono<InventoryItem> findByProductAndStore(String productId, String storeId);

    Flux<InventoryItem> findByStore(String storeId);

    Flux<InventoryItem> findByProduct(String productId);

    Mono<InventoryItem> save(InventoryItem item);

    Mono<Boolean> delete(String id);

    Mono<InventoryItem> updateWithVersionCheck(InventoryItem item, Long expectedVersion);

    Mono<Boolean> existsByProductAndStore(String productId, String storeId);

}
