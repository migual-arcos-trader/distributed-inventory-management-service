package com.meli.distributed_inventory_management_service.infrastructure.persistence.entity;

import java.time.LocalDateTime;

public class InventoryEntityMother {

    public static final String DEFAULT_ID = "entity-1";
    public static final String DEFAULT_PRODUCT_ID = "prod-1";
    public static final String DEFAULT_STORE_ID = "store-1";
    public static final Integer DEFAULT_CURRENT_STOCK = 100;
    public static final Integer DEFAULT_RESERVED_STOCK = 10;
    public static final Integer DEFAULT_MIN_STOCK = 5;
    public static final Integer DEFAULT_MAX_STOCK = 200;
    public static final Long DEFAULT_VERSION = 1L;

    public static InventoryEntity basicEntity() {
        return InventoryEntity.builder()
                .id(DEFAULT_ID)
                .productId(DEFAULT_PRODUCT_ID)
                .storeId(DEFAULT_STORE_ID)
                .currentStock(DEFAULT_CURRENT_STOCK)
                .reservedStock(DEFAULT_RESERVED_STOCK)
                .minimumStockLevel(DEFAULT_MIN_STOCK)
                .maximumStockLevel(DEFAULT_MAX_STOCK)
                .lastUpdated(LocalDateTime.now())
                .version(DEFAULT_VERSION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
