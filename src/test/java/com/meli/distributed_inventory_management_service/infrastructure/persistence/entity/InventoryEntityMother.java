package com.meli.distributed_inventory_management_service.infrastructure.persistence.entity;

import com.meli.distributed_inventory_management_service.infrastructure.persistence.repository.IntegrationTestsConstants;

import java.time.LocalDateTime;

public final class InventoryEntityMother {

    private InventoryEntityMother() {
    }

    public static InventoryEntity createDefaultTestEntity() {
        return InventoryEntity.builder()
                .id(IntegrationTestsConstants.TEST_ITEM_ID)
                .productId(IntegrationTestsConstants.TEST_PRODUCT_ID)
                .storeId(IntegrationTestsConstants.TEST_STORE_ID)
                .currentStock(IntegrationTestsConstants.INITIAL_CURRENT_STOCK)
                .reservedStock(IntegrationTestsConstants.INITIAL_RESERVED_STOCK)
                .minimumStockLevel(IntegrationTestsConstants.MINIMUM_STOCK_LEVEL)
                .maximumStockLevel(IntegrationTestsConstants.MAXIMUM_STOCK_LEVEL)
                .lastUpdated(LocalDateTime.now())
                .version(IntegrationTestsConstants.INITIAL_VERSION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static InventoryEntity createNativeTestEntity() {
        return InventoryEntity.builder()
                .id(IntegrationTestsConstants.NATIVE_TEST_ITEM_ID)
                .productId(IntegrationTestsConstants.NATIVE_PRODUCT_ID)
                .storeId(IntegrationTestsConstants.NATIVE_STORE_ID)
                .currentStock(IntegrationTestsConstants.INITIAL_CURRENT_STOCK)
                .reservedStock(IntegrationTestsConstants.INITIAL_RESERVED_STOCK)
                .minimumStockLevel(IntegrationTestsConstants.MINIMUM_STOCK_LEVEL)
                .maximumStockLevel(IntegrationTestsConstants.MAXIMUM_STOCK_LEVEL)
                .lastUpdated(LocalDateTime.now())
                .version(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static InventoryEntity createLowStockEntity() {
        return InventoryEntity.builder()
                .id(IntegrationTestsConstants.LOW_STOCK_ITEM_ID)
                .productId("low-prod")
                .storeId(IntegrationTestsConstants.TEST_STORE_ID)
                .currentStock(IntegrationTestsConstants.LOW_STOCK_VALUE)
                .reservedStock(IntegrationTestsConstants.INITIAL_RESERVED_STOCK)
                .minimumStockLevel(IntegrationTestsConstants.MINIMUM_STOCK_LEVEL)
                .maximumStockLevel(IntegrationTestsConstants.MAXIMUM_STOCK_LEVEL)
                .lastUpdated(LocalDateTime.now())
                .version(IntegrationTestsConstants.INITIAL_VERSION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static InventoryEntity createOverstockEntity() {
        return InventoryEntity.builder()
                .id(IntegrationTestsConstants.OVERSTOCK_ITEM_ID)
                .productId("over-prod")
                .storeId(IntegrationTestsConstants.TEST_STORE_ID)
                .currentStock(IntegrationTestsConstants.OVERSTOCK_VALUE)
                .reservedStock(IntegrationTestsConstants.ZERO_STOCK_LEVEL)
                .minimumStockLevel(IntegrationTestsConstants.MINIMUM_STOCK_LEVEL)
                .maximumStockLevel(IntegrationTestsConstants.MAXIMUM_STOCK_LEVEL)
                .lastUpdated(LocalDateTime.now())
                .version(IntegrationTestsConstants.INITIAL_VERSION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static InventoryEntity createEntityForStore(String storeId) {
        return InventoryEntity.builder()
                .id("item-store-2")
                .productId("prod-store-2")
                .storeId(storeId)
                .currentStock(IntegrationTestsConstants.UPDATED_75_STOCK)
                .reservedStock(IntegrationTestsConstants.NEW_ITEM_RESERVED_STOCK)
                .minimumStockLevel(IntegrationTestsConstants.NEW_ITEM_MIN_STOCK)
                .maximumStockLevel(IntegrationTestsConstants.UPDATED_STOCK)
                .lastUpdated(LocalDateTime.now())
                .version(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}