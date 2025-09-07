package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.DeleteInventoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DeleteInventoryUseCaseImpl implements DeleteInventoryUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Mono<Boolean> execute(String id) {
        return inventoryRepositoryPort.deleteById(id);
    }
}