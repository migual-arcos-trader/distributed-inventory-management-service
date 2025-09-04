package com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper;

public class MapperTestConstants {

    // Entity Constants
    public static final String ENTITY_ID = "entity-1";
    public static final String PRODUCT_ID = "prod-1";
    public static final String STORE_ID = "store-1";
    public static final Integer CURRENT_STOCK = 100;
    public static final Integer RESERVED_STOCK = 10;
    public static final Integer MINIMUM_STOCK_LEVEL = 5;
    public static final Integer MAXIMUM_STOCK_LEVEL = 200;
    public static final Long VERSION = 1L;
    public static final Long VERSION_5 = 5L;
    public static final Long VERSION_10 = 10L;
    // Error Messages
    public static final String VERSION_MISMATCH_ERROR = "Version mismatch. Expected: ";
    public static final String ACTUAL_VERSION = ", Actual: ";

    private MapperTestConstants() {
        // Utility class
    }

}