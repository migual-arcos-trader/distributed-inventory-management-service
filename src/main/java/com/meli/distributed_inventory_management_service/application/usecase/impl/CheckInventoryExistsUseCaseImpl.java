package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.CheckInventoryExistsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CheckInventoryExistsUseCaseImpl implements CheckInventoryExistsUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Mono<Boolean> execute(String productId, String storeId) {
        return inventoryRepositoryPort.existsByProductIdAndStoreId(productId, storeId);
    }
}