package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.GetInventoryByStoreUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GetInventoryByStoreUseCaseImpl implements GetInventoryByStoreUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Flux<InventoryItem> execute(String storeId) {
        return inventoryRepositoryPort.findByStoreId(storeId);
    }
}