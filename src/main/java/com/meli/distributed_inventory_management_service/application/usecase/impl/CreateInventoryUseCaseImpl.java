package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort;
import com.meli.distributed_inventory_management_service.application.usecase.CreateInventoryUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Component
@RequiredArgsConstructor
public class CreateInventoryUseCaseImpl implements CreateInventoryUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    public Mono<InventoryItem> execute(String productId, String storeId, Integer initialStock) {
        return inventoryRepositoryPort.existsByProductIdAndStoreId(productId, storeId)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException(ERROR_INVENTORY_ALREADY_EXISTS));
                    }

                    InventoryItem newItem = InventoryItem.builder()
                            .productId(productId)
                            .storeId(storeId)
                            .currentStock(initialStock)
                            .reservedStock(DEFAULT_RESERVED_STOCK)
                            .minimumStockLevel(DEFAULT_MINIMUM_STOCK)
                            .maximumStockLevel(DEFAULT_MAXIMUM_STOCK)
                            .lastUpdated(LocalDateTime.now())
                            .version(INITIAL_VERSION)
                            .build();

                    return inventoryRepositoryPort.save(newItem);
                });
    }
}