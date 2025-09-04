package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

public class IntegrationTestsConstants {

    // IDs
    public static final String TEST_ITEM_ID = "integration-test-item";
    public static final String TEST_PRODUCT_ID = "integration-prod-1";
    public static final String TEST_STORE_ID = "integration-store-1";
    public static final String NATIVE_TEST_ITEM_ID = "native-test-item";
    public static final String NATIVE_PRODUCT_ID = "native-prod-1";
    public static final String NATIVE_STORE_ID = "native-store-1";
    public static final String DIFFERENT_STORE_ID = "different-store";
    public static final String LOW_STOCK_ITEM_ID = "low-stock-item";
    public static final String OVERSTOCK_ITEM_ID = "overstock-item";
    // Stock values
    public static final int INITIAL_CURRENT_STOCK = 100;
    public static final int INITIAL_RESERVED_STOCK = 10;
    public static final int UPDATED_STOCK = 150;
    public static final int NATIVE_UPDATED_STOCK = 200;
    public static final int ATOMIC_UPDATED_STOCK = 250;
    public static final int UPDATED_75_STOCK = 75;
    public static final int NEW_ITEM_STOCK = 50;
    public static final int LOW_STOCK_THRESHOLD = 10;
    public static final int LOW_STOCK_VALUE = 10;
    public static final int OVERSTOCK_VALUE = 300;
    public static final int QUANTITY_TO_RESERVE = 5;
    public static final int QUANTITY_TO_RELEASE = 3;
    // Stock levels
    public static final int ZERO_STOCK_LEVEL = 0;
    public static final int MINIMUM_STOCK_LEVEL = 5;
    public static final int MAXIMUM_STOCK_LEVEL = 200;
    public static final int NEW_ITEM_MAX_STOCK = 100;
    public static final int NEW_ITEM_MIN_STOCK = 2;
    public static final int NEW_ITEM_RESERVED_STOCK = 5;
    // Versions
    public static final Long ZERO_VERSION = 0L;
    public static final Long INITIAL_VERSION = 1L;
    public static final Long WRONG_VERSION_OFFSET = 5L;
    // Timeouts
    public static final int TEST_TIMEOUT_SECONDS = 5;
    public static final int UNIT_EXPECT = 1;

    private IntegrationTestsConstants() {
    }
}