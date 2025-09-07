package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.GetAllInventoryUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GetAllInventoryUseCaseImpl implements GetAllInventoryUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Flux<InventoryItem> execute() {
        return inventoryRepositoryPort.findAll();
    }
}