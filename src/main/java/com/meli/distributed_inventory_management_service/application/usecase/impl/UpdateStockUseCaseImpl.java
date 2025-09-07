package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.usecase.UpdateStockUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import com.meli.distributed_inventory_management_service.domain.service.InventoryDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UpdateStockUseCaseImpl implements UpdateStockUseCase {

    private final InventoryDomainService inventoryDomainService;

    @Override
    public Mono<InventoryItem> execute(String productId, String storeId, Integer quantity, String updateType) {
        UpdateType type;
        try {
            type = UpdateType.valueOf(updateType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid update type: " + updateType));
        }

        return inventoryDomainService.updateStockWithRetry(productId, storeId, quantity, type);
    }
}