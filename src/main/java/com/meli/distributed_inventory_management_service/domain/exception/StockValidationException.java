package com.meli.distributed_inventory_management_service.domain.exception;

public class StockValidationException extends InventoryException {

    public StockValidationException(String message, String details) {
        super(message, "STOCK_VALIDATION_ERROR", details);
    }

    public StockValidationException(String message, String details, Throwable cause) {
        super(message, "STOCK_VALIDATION_ERROR", details, cause);
    }

}
