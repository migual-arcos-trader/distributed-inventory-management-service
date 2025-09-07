package com.meli.distributed_inventory_management_service.application.usecase;

import reactor.core.publisher.Mono;

public interface CheckInventoryExistsUseCase {
    Mono<Boolean> execute(String productId, String storeId);
}