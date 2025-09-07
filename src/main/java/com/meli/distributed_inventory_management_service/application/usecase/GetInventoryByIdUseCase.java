package com.meli.distributed_inventory_management_service.application.usecase;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import reactor.core.publisher.Mono;

public interface GetInventoryByIdUseCase {
    Mono<InventoryItem> execute(String id);
}