package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.GetInventoryByIdUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetInventoryByIdUseCaseImpl implements GetInventoryByIdUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Mono<InventoryItem> execute(String id) {
        return inventoryRepositoryPort.findById(id);
    }
}
