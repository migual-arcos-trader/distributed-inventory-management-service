package com.meli.distributed_inventory_management_service.infrastructure.web.constants;

public class ControllerTestConstants {

    private ControllerTestConstants() {}

    // Test Data
    public static final String TEST_ITEM_ID = "test-item-1";
    public static final String TEST_PRODUCT_ID = "test-prod-1";
    public static final String TEST_STORE_ID = "test-store-1";
    public static final String TEST_ITEM_ID_2 = "test-item-2";
    public static final String TEST_PRODUCT_ID_2 = "test-prod-2";
    public static final String NON_EXISTENT_ID = "non-existent-id";

    // Stock Values
    public static final int INITIAL_STOCK = 100;
    public static final int RESERVED_STOCK = 10;
    public static final int QUANTITY_TO_RESERVE = 5;
    public static final int QUANTITY_TO_UPDATE = 25;
    public static final int QUANTITY_TO_CHECK = 50;
    public static final int ZERO_STOCK = 0;

    // API Paths
    public static final String INVENTORY_BASE_PATH = "/api/v1/inventory";
    public static final String INVENTORY_BY_ID_PATH = INVENTORY_BASE_PATH + "/{id}";
    public static final String INVENTORY_BY_STORE_PATH = INVENTORY_BASE_PATH + "/store/{storeId}";
    public static final String INVENTORY_BY_PRODUCT_PATH = INVENTORY_BASE_PATH + "/product/{productId}";
    public static final String UPDATE_STOCK_PATH = INVENTORY_BASE_PATH + "/stock";
    public static final String RESERVE_STOCK_PATH = INVENTORY_BASE_PATH + "/{productId}/{storeId}/reserve";
    public static final String RELEASE_STOCK_PATH = INVENTORY_BASE_PATH + "/{productId}/{storeId}/release";
    public static final String AVAILABLE_STOCK_PATH = INVENTORY_BASE_PATH + "/{productId}/{storeId}/available";
    public static final String CAN_FULFILL_PATH = INVENTORY_BASE_PATH + "/{productId}/{storeId}/can-fulfill";

    // Security
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TEST_TOKEN = "test-jwt-token";

    // Responses
    public static final int OK_STATUS = 200;
    public static final int CREATED_STATUS = 201;
    public static final int NO_CONTENT_STATUS = 204;
    public static final int BAD_REQUEST_STATUS = 400;
    public static final int UNAUTHORIZED_STATUS = 401;
    public static final int NOT_FOUND_STATUS = 404;
    public static final int CONFLICT_STATUS = 409;
}