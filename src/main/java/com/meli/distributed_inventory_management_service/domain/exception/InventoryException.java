package com.meli.distributed_inventory_management_service.domain.exception;

import lombok.Getter;

@Getter
public class InventoryException extends RuntimeException {
    private final String errorCode;
    private final String details;

    public InventoryException(String message, String errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public InventoryException(String message, String errorCode, String details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

}
