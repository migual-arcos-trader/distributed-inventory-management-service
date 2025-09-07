package com.meli.distributed_inventory_management_service.application.constants;

import com.meli.distributed_inventory_management_service.domain.model.EventStatus;

public class ApplicationConstants {

    private ApplicationConstants() {}

    // Stock Constants
    public static final int MIN_AVAILABLE_STOCK = 0;
    public static final int MAX_AVAILABLE_STOCK = 1000;
    public static final int DEFAULT_MINIMUM_STOCK = 10;
    public static final int DEFAULT_MAXIMUM_STOCK = 500;
    public static final int DEFAULT_RESERVED_STOCK = 0;

    // Retry Configuration
    public static final int MAX_RETRY_ATTEMPTS = 5;
    public static final int RETRY_INITIAL_DELAY_MS = 100;
    public static final int RETRY_MAX_DELAY_MS = 1000;
    public static final int RETRY_MULTIPLIER = 2;

    // Versioning
    public static final long INITIAL_VERSION = 0L;

    // Error Messages
    public static final String ERROR_INVENTORY_ALREADY_EXISTS = "Inventory item already exists";
    public static final String ERROR_INSUFFICIENT_STOCK = "Insufficient stock";
    public static final String ERROR_CONCURRENT_UPDATE = "Concurrent update detected";
    public static final String ERROR_INVALID_UPDATE_TYPE = "Invalid update type";
    public static final String ERROR_RESERVATION_NOT_FOUND = "Reservation not found";
    public static final String ERROR_EVENT_NOT_FOUND = "Event not found";
    public static final String ERROR_RESERVATION_EXPIRED = "Reservation expired";

    // Validation Messages
    public static final String VALIDATION_PRODUCT_ID_REQUIRED = "Product ID is required";
    public static final String VALIDATION_STORE_ID_REQUIRED = "Store ID is required";
    public static final String VALIDATION_STOCK_REQUIRED = "Current stock is required";
    public static final String VALIDATION_STOCK_POSITIVE = " must be zero or positive";
    public static final String VALIDATION_QUANTITY_POSITIVE = "Quantity must be positive";
    public static final String VALIDATION_RESERVATION_ID_REQUIRED = "Reservation ID is required";
    public static final String VALIDATION_EVENT_ID_REQUIRED = "Event ID is required";

    // Reservation
    public static final int RESERVATION_TIMEOUT_MINUTES = 30;
    public static final String RESERVATION_PREFIX = "RES_";

    // Event
    public static final String EVENT_SOURCE_SYSTEM = "SYSTEM";
    public static final String EVENT_SOURCE_API = "API";
    public static final int EVENT_MAX_RETRIES = 3;

    // Status Constants (usando los enums del dominio)
    public static final String STATUS_PENDING = EventStatus.PENDING.name();
    public static final String STATUS_RESERVED = "RESERVED"; // Para reservas
    public static final String STATUS_CONFIRMED = "CONFIRMED"; // Para reservas
    public static final String STATUS_RELEASED = "RELEASED"; // Para reservas
    public static final String STATUS_EXPIRED = "EXPIRED"; // Para reservas
    public static final String STATUS_PROCESSED = EventStatus.PROCESSED.name();
    public static final String STATUS_FAILED = EventStatus.FAILED.name();
    public static final String STATUS_COMPENSATED = EventStatus.COMPENSATED.name();
    public static final String STATUS_ROLLED_BACK = EventStatus.ROLLED_BACK.name();
}