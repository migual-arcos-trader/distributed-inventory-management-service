package com.meli.distributed_inventory_management_service.application.usecase;

import reactor.core.publisher.Mono;

public interface DeleteInventoryUseCase {
    Mono<Boolean> execute(String id);
}