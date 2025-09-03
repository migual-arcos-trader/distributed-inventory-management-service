package com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper;

import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;

import java.time.LocalDateTime;

import static com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper.MapperTestConstants.*;

public class EntityTestFactory {

    private EntityTestFactory() {
        // Utility class
    }

    public static InventoryEntity createBasicInventoryEntity() {
        return InventoryEntity.builder()
                .id(ENTITY_ID)
                .productId(PRODUCT_ID)
                .storeId(STORE_ID)
                .currentStock(CURRENT_STOCK)
                .reservedStock(RESERVED_STOCK)
                .minimumStockLevel(MINIMUM_STOCK_LEVEL)
                .maximumStockLevel(MAXIMUM_STOCK_LEVEL)
                .lastUpdated(LocalDateTime.now())
                .version(VERSION)
                .build();
    }

    public static InventoryEntity createInventoryEntityWithVersion(Long version) {
        return InventoryEntity.builder()
                .id(ENTITY_ID)
                .productId(PRODUCT_ID)
                .storeId(STORE_ID)
                .currentStock(CURRENT_STOCK)
                .reservedStock(RESERVED_STOCK)
                .minimumStockLevel(MINIMUM_STOCK_LEVEL)
                .maximumStockLevel(MAXIMUM_STOCK_LEVEL)
                .lastUpdated(LocalDateTime.now())
                .version(version)
                .build();
    }

    public static InventoryEntity createMinimalInventoryEntity(String id, Long version) {
        return InventoryEntity.builder()
                .id(id)
                .productId(PRODUCT_ID)
                .storeId(STORE_ID)
                .currentStock(CURRENT_STOCK)
                .reservedStock(RESERVED_STOCK)
                .minimumStockLevel(MINIMUM_STOCK_LEVEL)
                .maximumStockLevel(MAXIMUM_STOCK_LEVEL)
                .lastUpdated(LocalDateTime.now())
                .version(version)
                .build();
    }

}
