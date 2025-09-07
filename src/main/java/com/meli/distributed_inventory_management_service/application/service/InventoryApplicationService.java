package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.usecase.*;
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
    private final GetAllInventoryUseCase getAllInventoryUseCase;
    private final GetInventoryByIdUseCase getInventoryByIdUseCase;
    private final GetInventoryByProductAndStoreUseCase getInventoryByProductAndStoreUseCase;
    private final GetInventoryByStoreUseCase getInventoryByStoreUseCase;
    private final GetInventoryByProductUseCase getInventoryByProductUseCase;
    private final CreateInventoryUseCase createInventoryUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseReservationUseCase releaseReservationUseCase;
    private final DeleteInventoryUseCase deleteInventoryUseCase;
    private final CheckInventoryExistsUseCase checkInventoryExistsUseCase;

    public Flux<InventoryItem> getAllInventory() {
        return getAllInventoryUseCase.execute();
    }

    public Mono<InventoryItem> getInventoryById(String id) {
        return getInventoryByIdUseCase.execute(id);
    }

    public Flux<InventoryItem> getInventoryByStore(String storeId) {
        return getInventoryByStoreUseCase.execute(storeId);
    }

    public Flux<InventoryItem> getInventoryByProduct(String productId) {
        return getInventoryByProductUseCase.execute(productId);
    }

    public Mono<InventoryItem> createInventory(String productId, String storeId, Integer initialStock) {
        return createInventoryUseCase.execute(productId, storeId, initialStock);
    }

    public Mono<InventoryItem> updateStock(String productId, String storeId, Integer quantity, UpdateType updateType) {
        return updateStockUseCase.execute(productId, storeId, quantity, updateType.toString());
    }

    public Mono<InventoryItem> reserveStock(String productId, String storeId, Integer quantity) {
        return reserveStockUseCase.execute(productId, storeId, quantity);
    }

    public Mono<InventoryItem> releaseReservedStock(String productId, String storeId, Integer quantity) {
        return releaseReservationUseCase.execute(productId, storeId, quantity);
    }

    public Mono<Boolean> deleteInventory(String id) {
        return deleteInventoryUseCase.execute(id);
    }

    public Mono<InventoryItem> updateStockWithRetry(String productId, String storeId, Integer quantity, UpdateType updateType) {
        return domainInventoryService.updateStockWithRetry(productId, storeId, quantity, updateType);
    }

    public Mono<Integer> getAvailableStock(String productId, String storeId) {
        return domainInventoryService.getAvailableStock(productId, storeId);
    }

}