package com.meli.distributed_inventory_management_service.domain.exception;


public class ConcurrentUpdateException extends InventoryException {

    public ConcurrentUpdateException(String productId, String storeId,
                                     Long expectedVersion, Long actualVersion) {
        super("Concurrent modification detected", "CONCURRENT_UPDATE",
                String.format("Product: %s, Store: %s, Expected version: %d, Actual version: %d",
                        productId, storeId, expectedVersion, actualVersion));
    }

}