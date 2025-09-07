package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.GetInventoryByProductAndStoreUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetInventoryByProductAndStoreUseCaseImpl implements GetInventoryByProductAndStoreUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Mono<InventoryItem> execute(String productId, String storeId) {
        return inventoryRepositoryPort.findByProductIdAndStoreId(productId, storeId);
    }
}