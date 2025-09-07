package com.meli.distributed_inventory_management_service.application.constants;

public class ApplicationTestConstants {

    // Reservation Test Data
    public static final String RESERVATION_ID = "RES_123456";
    public static final String PRODUCT_ID = "prod-test-1";
    public static final String STORE_ID = "store-test-1";
    public static final Integer QUANTITY = 10;
    public static final String CORRELATION_ID = "corr-123456";
    public static final String RESERVATION_STATUS_RESERVED = "RESERVED";
    public static final String RESERVATION_STATUS_CONFIRMED = "CONFIRMED";
    public static final String RESERVATION_STATUS_RELEASED = "RELEASED";
    // Event Test Data
    public static final String EVENT_ID = "event-123456";
    public static final String EVENT_SOURCE = "TEST_SOURCE";
    public static final String COMPENSATION_REASON = "Test compensation";
    // General Test Data
    public static final String NON_EXISTENT_ID = "non-existent-id";
    public static final String ERROR_MESSAGE = "Test error message";

    private ApplicationTestConstants() {
    }
}