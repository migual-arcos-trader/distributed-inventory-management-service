package com.meli.distributed_inventory_management_service.domain.exception;


public class InsufficientStockException extends InventoryException {
    public InsufficientStockException(String productId, String storeId,
                                      Integer requested, Integer available) {
        super("Insufficient stock", "INSUFFICIENT_STOCK",
                String.format("Product: %s, Store: %s, Requested: %d, Available: %d",
                        productId, storeId, requested, available));
    }
}