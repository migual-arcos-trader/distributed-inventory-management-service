package com.meli.distributed_inventory_management_service.infrastructure.persistence.adapter;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final InventoryRepository inventoryRepository;

    @Override
    public Mono<InventoryItem> save(InventoryItem inventoryItem) {
        return inventoryRepository.save(inventoryItem);
    }

    @Override
    public Mono<InventoryItem> findById(String id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public Mono<InventoryItem> findByProductIdAndStoreId(String productId, String storeId) {
        return inventoryRepository.findByProductAndStore(productId, storeId);
    }

    @Override
    public Flux<InventoryItem> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Flux<InventoryItem> findByStoreId(String storeId) {
        return inventoryRepository.findByStore(storeId);
    }

    @Override
    public Flux<InventoryItem> findByProductId(String productId) {
        return inventoryRepository.findByProduct(productId);
    }

    @Override
    public Mono<Boolean> deleteById(String id) {
        return inventoryRepository.delete(id);
    }

    @Override
    public Mono<Boolean> existsByProductIdAndStoreId(String productId, String storeId) {
        return inventoryRepository.existsByProductAndStore(productId, storeId);
    }

}
