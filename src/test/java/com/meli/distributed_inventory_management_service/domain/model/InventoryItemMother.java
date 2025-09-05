package com.meli.distributed_inventory_management_service.domain.model;

import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.repository.IntegrationTestsConstants;

import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryItemMother {

    public static final String DEFAULT_ID = "item-1";
    public static final String DEFAULT_PRODUCT_ID = "prod-1";
    public static final String DEFAULT_STORE_ID = "store-1";
    public static final Integer DEFAULT_CURRENT_STOCK = 100;
    public static final Integer DEFAULT_RESERVED_STOCK = 10;
    public static final Integer DEFAULT_MIN_STOCK = 5;
    public static final Integer DEFAULT_MAX_STOCK = 200;
    public static final Long DEFAULT_VERSION = 1L;
    public static final Long ZERO_VERSION = 0L;
    public static final Integer MAXIMUM_STOCK = 50;
    public static final Integer UPDATE_STOCK = 50;
    public static final Integer WITHOUT_STOCK = 0;

    public static InventoryItem.InventoryItemBuilder basic() {
        return InventoryItem.builder()
                .id(DEFAULT_ID)
                .productId(DEFAULT_PRODUCT_ID)
                .storeId(DEFAULT_STORE_ID)
                .currentStock(DEFAULT_CURRENT_STOCK)
                .reservedStock(DEFAULT_RESERVED_STOCK)
                .minimumStockLevel(DEFAULT_MIN_STOCK)
                .maximumStockLevel(DEFAULT_MAX_STOCK)
                .lastUpdated(LocalDateTime.now())
                .version(DEFAULT_VERSION);
    }

    public static InventoryItem basicItem() {
        return basic().build();
    }

    public static InventoryItem withStock(Integer current, Integer reserved) {
        return basic()
                .currentStock(current)
                .reservedStock(reserved)
                .build();
    }

    public static InventoryItem withVersion(Long version) {
        return basic().version(version).build();
    }

    public static InventoryItem newItem() {
        return InventoryItem.builder()
                .productId(DEFAULT_PRODUCT_ID)
                .storeId(DEFAULT_STORE_ID)
                .currentStock(WITHOUT_STOCK)
                .reservedStock(WITHOUT_STOCK)
                .minimumStockLevel(WITHOUT_STOCK)
                .maximumStockLevel(MAXIMUM_STOCK)
                .lastUpdated(LocalDateTime.now())
                .version(ZERO_VERSION)
                .build();
    }

    public static InventoryItem updatedNewItem() {
        return InventoryItem.builder()
                .productId(DEFAULT_PRODUCT_ID)
                .storeId(DEFAULT_STORE_ID)
                .currentStock(UPDATE_STOCK)
                .reservedStock(WITHOUT_STOCK)
                .minimumStockLevel(WITHOUT_STOCK)
                .maximumStockLevel(MAXIMUM_STOCK)
                .lastUpdated(LocalDateTime.now())
                .version(DEFAULT_VERSION)
                .build();
    }

    public static InventoryItem createNativeTestItem(InventoryEntity entity) {
        return InventoryItem.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .storeId(entity.getStoreId())
                .currentStock(IntegrationTestsConstants.NATIVE_UPDATED_STOCK)
                .reservedStock(entity.getReservedStock())
                .minimumStockLevel(entity.getMinimumStockLevel())
                .maximumStockLevel(entity.getMaximumStockLevel())
                .lastUpdated(LocalDateTime.now())
                .version(entity.getVersion() != null ? entity.getVersion() + 1 : 1L)
                .build();
    }

    public static InventoryItem createAtomicTestItem(InventoryEntity entity) {
        return InventoryItem.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .storeId(entity.getStoreId())
                .currentStock(IntegrationTestsConstants.ATOMIC_UPDATED_STOCK)
                .reservedStock(entity.getReservedStock())
                .minimumStockLevel(entity.getMinimumStockLevel())
                .maximumStockLevel(entity.getMaximumStockLevel())
                .lastUpdated(LocalDateTime.now())
                .version(entity.getVersion() != null ? entity.getVersion() + 1 : 1L)
                .build();
    }

    public static InventoryItem createNewItem() {
        String newId = "new-item-" + UUID.randomUUID();
        return InventoryItem.builder()
                .id(newId)
                .productId("new-prod-1")
                .storeId("new-store-1")
                .currentStock(IntegrationTestsConstants.NEW_ITEM_STOCK)
                .reservedStock(IntegrationTestsConstants.NEW_ITEM_RESERVED_STOCK)
                .minimumStockLevel(IntegrationTestsConstants.NEW_ITEM_MIN_STOCK)
                .maximumStockLevel(IntegrationTestsConstants.NEW_ITEM_MAX_STOCK)
                .lastUpdated(LocalDateTime.now())
                .version(null)
                .build();
    }

    public static InventoryItem createUpdatedItem(InventoryItem existingItem) {
        return InventoryItem.builder()
                .id(existingItem.getId())
                .productId(existingItem.getProductId())
                .storeId(existingItem.getStoreId())
                .currentStock(IntegrationTestsConstants.UPDATED_STOCK)
                .reservedStock(existingItem.getReservedStock())
                .minimumStockLevel(existingItem.getMinimumStockLevel())
                .maximumStockLevel(existingItem.getMaximumStockLevel())
                .lastUpdated(LocalDateTime.now())
                .version(existingItem.getVersion())
                .build();
    }
}