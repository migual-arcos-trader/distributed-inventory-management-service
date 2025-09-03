package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestDataFactory {

    public static final String TEST_PRODUCT_ID = "test-prod-1";
    public static final String TEST_STORE_ID = "test-store-1";
    public static final String TEST_ID = "test-item-1";
    public static final Integer TEST_CURRENT_STOCK = 100;
    public static final Integer TEST_RESERVED_STOCK = 10;
    public static final Long TEST_VERSION = 1L;

    public static InventoryEntity createInventoryEntity() {
        return InventoryEntity.builder()
                .id(UUID.randomUUID().toString())
                .productId(TEST_PRODUCT_ID)
                .storeId(TEST_STORE_ID)
                .currentStock(TEST_CURRENT_STOCK)
                .reservedStock(TEST_RESERVED_STOCK)
                .minimumStockLevel(5)
                .maximumStockLevel(200)
                .lastUpdated(LocalDateTime.now())
                .version(TEST_VERSION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static InventoryEntity createInventoryEntity(String productId, String storeId, Integer currentStock, Long version) {
        return InventoryEntity.builder()
                .id(UUID.randomUUID().toString())
                .productId(productId)
                .storeId(storeId)
                .currentStock(currentStock)
                .reservedStock(TEST_RESERVED_STOCK)
                .minimumStockLevel(5)
                .maximumStockLevel(200)
                .lastUpdated(LocalDateTime.now())
                .version(version)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
