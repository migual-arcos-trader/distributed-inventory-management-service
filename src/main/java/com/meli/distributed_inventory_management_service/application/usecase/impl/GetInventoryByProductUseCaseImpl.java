package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.GetInventoryByProductUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GetInventoryByProductUseCaseImpl implements GetInventoryByProductUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Flux<InventoryItem> execute(String productId) {
        return inventoryRepositoryPort.findByProductId(productId);
    }
}