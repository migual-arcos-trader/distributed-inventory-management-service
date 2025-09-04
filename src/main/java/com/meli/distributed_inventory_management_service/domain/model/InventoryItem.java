package com.meli.distributed_inventory_management_service.domain.model;

import lombok.Builder;
import lombok.Value;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Value
@Builder

public class InventoryItem {

    public static final int MIN_AVAILABLE_STOCK = 0;
    public static final int PLUS_VERSION = 1;

    String id;
    String productId;
    String storeId;
    Integer currentStock;
    Integer reservedStock;
    Integer minimumStockLevel;
    Integer maximumStockLevel;
    LocalDateTime lastUpdated;
    Long version;

    public Mono<Integer> getAvailableStock() {
        return Mono.just(Math.max(MIN_AVAILABLE_STOCK, currentStock - reservedStock));
    }

    public Mono<Boolean> canFulfillOrder(Integer quantity) {
        return getAvailableStock().map(available -> available >= quantity && available > MIN_AVAILABLE_STOCK);
    }

    public Mono<InventoryItem> reserveStock(Integer quantity) {
        if (quantity <= MIN_AVAILABLE_STOCK) {
            return Mono.error(new IllegalArgumentException("Quantity must be positive"));
        }

        return getAvailableStock().flatMap(available -> {
            if (available < quantity) {
                return Mono.error(new IllegalStateException(
                        String.format("Insufficient stock. Available: %d, Requested: %d", available, quantity)
                ));
            }

            return Mono.just(InventoryItem.builder()
                    .id(this.id)
                    .productId(this.productId)
                    .storeId(this.storeId)
                    .currentStock(this.currentStock)
                    .reservedStock(this.reservedStock + quantity)
                    .minimumStockLevel(this.minimumStockLevel)
                    .maximumStockLevel(this.maximumStockLevel)
                    .lastUpdated(LocalDateTime.now())
                    .version(this.version + PLUS_VERSION)
                    .build());
        });
    }

    public Mono<InventoryItem> releaseReservedStock(Integer quantity) {
        if (quantity <= MIN_AVAILABLE_STOCK) {
            return Mono.error(new IllegalArgumentException("Quantity must be positive"));
        }
        if (this.reservedStock < quantity) {
            return Mono.error(new IllegalStateException(
                    String.format("Cannot release more than reserved. Reserved: %d, Requested: %d",
                            this.reservedStock, quantity)
            ));
        }
        return Mono.just(InventoryItem.builder()
                .id(this.id)
                .productId(this.productId)
                .storeId(this.storeId)
                .currentStock(this.currentStock)
                .reservedStock(this.reservedStock - quantity)
                .minimumStockLevel(this.minimumStockLevel)
                .maximumStockLevel(this.maximumStockLevel)
                .lastUpdated(LocalDateTime.now())
                .version(this.version + PLUS_VERSION)
                .build());
    }

    public Mono<InventoryItem> updateStock(Integer quantity, UpdateType type) {
        return Mono.fromCallable(() -> {
            int newStock = calculateNewStockBasedOnType(quantity, type);
            validateStockLevels(newStock);
            return InventoryItem.builder()
                    .id(this.id)
                    .productId(this.productId)
                    .storeId(this.storeId)
                    .currentStock(newStock)
                    .reservedStock(this.reservedStock)
                    .minimumStockLevel(this.minimumStockLevel)
                    .maximumStockLevel(this.maximumStockLevel)
                    .lastUpdated(LocalDateTime.now())
                    .version(this.version + PLUS_VERSION)
                    .build();
        });
    }

    private void validateStockLevels(Integer newStock) {
        if (newStock < MIN_AVAILABLE_STOCK) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (maximumStockLevel != null && newStock > maximumStockLevel) {
            throw new IllegalArgumentException(
                    String.format("Stock exceeds maximum level. Max: %d, Attempted: %d",
                            maximumStockLevel, newStock)
            );
        }
    }

    private int calculateNewStockBasedOnType(Integer quantity, UpdateType type) {
        return switch (type) {
            case PURCHASE, ADJUSTMENT -> this.currentStock + quantity;
            case SALE -> this.currentStock - quantity;
            case RESTOCK -> quantity;
            default -> throw new IllegalArgumentException("Invalid update type");
        };
    }

}
