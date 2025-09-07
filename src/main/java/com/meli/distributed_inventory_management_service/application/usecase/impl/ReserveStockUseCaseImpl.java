package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.usecase.ReserveStockUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReserveStockUseCaseImpl implements ReserveStockUseCase {

    private final InventoryService inventoryService;

    @Override
    public Mono<InventoryItem> execute(String productId, String storeId, Integer quantity) {
        return inventoryService.reserveStock(productId, storeId, quantity);
    }
}