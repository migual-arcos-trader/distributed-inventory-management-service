package com.meli.distributed_inventory_management_service.application.usecase;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import reactor.core.publisher.Flux;

public interface GetInventoryByProductUseCase {
    Flux<InventoryItem> execute(String productId);
}