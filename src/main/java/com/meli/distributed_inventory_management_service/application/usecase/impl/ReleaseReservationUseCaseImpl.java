package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.usecase.ReleaseReservationUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.service.InventoryDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReleaseReservationUseCaseImpl implements ReleaseReservationUseCase {

    private final InventoryDomainService inventoryDomainService;

    @Override
    public Mono<InventoryItem> execute(String productId, String storeId, Integer quantity) {
        return inventoryDomainService.releaseReservedStock(productId, storeId, quantity);
    }
}