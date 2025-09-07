package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import com.meli.distributed_inventory_management_service.domain.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private final InventoryService domainInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public Mono<InventoryItem> updateStockWithRetry(String productId, String storeId, int quantity, UpdateType updateType) {
        return domainInventoryService.updateStockWithRetry(productId, storeId, quantity, updateType);
    }

    public Mono<InventoryItem> reserveStock(String productId, String storeId, int quantity) {
        return domainInventoryService.reserveStock(productId, storeId, quantity);
    }

    public Mono<InventoryItem> releaseReservedStock(String productId, String storeId, int quantity) {
        return domainInventoryService.releaseReservedStock(productId, storeId, quantity);
    }

    public Mono<Integer> getAvailableStock(String productId, String storeId) {
        return domainInventoryService.getAvailableStock(productId, storeId);
    }

    public Flux<InventoryItem> getAllInventory() {
        return inventoryRepositoryPort.findAll();
    }

    public Mono<InventoryItem> getInventoryById(String id) {
        return inventoryRepositoryPort.findById(id);
    }

    public Flux<InventoryItem> getInventoryByStore(String storeId) {
        return inventoryRepositoryPort.findByStoreId(storeId);
    }

    public Flux<InventoryItem> getInventoryByProduct(String productId) {
        return inventoryRepositoryPort.findByProductId(productId);
    }

    public Mono<Boolean> deleteInventory(String id) {
        return inventoryRepositoryPort.deleteById(id);
    }

    public Mono<InventoryItem> createInventory(String productId, String storeId, int initialStock) {
        return inventoryRepositoryPort.existsByProductIdAndStoreId(productId, storeId)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Inventory item already exists"));
                    }

                    InventoryItem newItem = InventoryItem.builder()
                            .productId(productId)
                            .storeId(storeId)
                            .currentStock(initialStock)
                            .reservedStock(0)
                            .minimumStockLevel(10)
                            .maximumStockLevel(500)
                            .build();

                    return inventoryRepositoryPort.save(newItem);
                });
    }

}
