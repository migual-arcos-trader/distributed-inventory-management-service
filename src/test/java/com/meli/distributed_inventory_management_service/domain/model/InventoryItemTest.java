package com.meli.distributed_inventory_management_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.*;
import static org.junit.jupiter.api.Assertions.*;

class InventoryItemTest {

    @Test
    @DisplayName("Should calculate available stock correctly")
    void shouldCalculateAvailableStock() {
        // Arrange
        int current = 100;
        int reserved = 20;
        int expect = 80;
        InventoryItem item = withStock(current, reserved);

        // Act
        Mono<Integer> availableStockMono = item.getAvailableStock();

        // Assert
        StepVerifier.create(availableStockMono)
                .expectNext(expect)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return true when can fulfill order with sufficient stock")
    void shouldFulfillOrderWhenSufficientStock() {
        // Arrange
        int current = 100;
        int reserved = 50;
        Integer orderQuantity = 50;
        InventoryItem item = withStock(current, reserved);

        // Act
        Mono<Boolean> canFulfillMono = item.canFulfillOrder(orderQuantity);

        // Assert
        StepVerifier.create(canFulfillMono)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when cannot fulfill order with insufficient stock")
    void shouldNotFulfillOrderWhenInsufficientStock() {
        // Arrange
        int current = 100;
        int reserved = 95;
        Integer orderQuantity = 6;
        InventoryItem item = withStock(current, reserved);

        // Act
        Mono<Boolean> canFulfillMono = item.canFulfillOrder(orderQuantity);

        // Assert
        StepVerifier.create(canFulfillMono)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should successfully reserve stock when available")
    void shouldReserveStockWhenAvailable() {
        // Arrange
        int current = 100;
        int reserved = 10;
        int quantityToReserve = 20;
        int expectReservedStock = reserved + quantityToReserve;
        Long expectVersion = 2L;
        InventoryItem item = withStock(current, reserved);

        // Act
        Mono<InventoryItem> resultMono = item.reserveStock(quantityToReserve);

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(updatedItem -> {
                    assertEquals(expectReservedStock, updatedItem.getReservedStock());
                    assertEquals(current, updatedItem.getCurrentStock());
                    assertEquals(expectVersion, updatedItem.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail to reserve stock when insufficient available")
    void shouldFailReserveStockWhenInsufficient() {
        // Arrange
        int current = 100;
        int reserved = 95;
        Integer quantityToReserve = 6;
        InventoryItem item = withStock(current, reserved);

        // Act
        Mono<InventoryItem> resultMono = item.reserveStock(quantityToReserve);

        // Assert
        StepVerifier.create(resultMono)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    @DisplayName("Should update stock correctly for PURCHASE operation")
    void shouldUpdateStockForPurchase() {
        // Arrange
        int current = 100;
        int reserved = 10;
        int quantity = 25;
        int expectCurrentStock = current + quantity;
        Long expectVersion = 2L;

        InventoryItem item = withStock(current, reserved);

        // Act
        Mono<InventoryItem> resultMono = item.updateStock(quantity, UpdateType.PURCHASE);

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(updatedItem -> {
                    assertEquals(expectCurrentStock, updatedItem.getCurrentStock());
                    assertEquals(reserved, updatedItem.getReservedStock());
                    assertEquals(expectVersion, updatedItem.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate stock levels and throw exception when negative")
    void shouldValidateStockLevelsAndThrowWhenNegative() {
        // Arrange
        int current = 100;
        int reserved = 10;
        Integer quantity = 150;
        InventoryItem item = withStock(current, reserved);

        // Act
        Mono<InventoryItem> resultMono = item.updateStock(quantity, UpdateType.SALE);

        // Assert
        StepVerifier.create(resultMono)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("Should create item with specific version using withVersion factory method")
    void shouldCreateItemWithSpecificVersion() {
        // Arrange
        Long specificVersion = 5L;

        // Act
        InventoryItem item = withVersion(specificVersion);

        // Assert
        assertNotNull(item, "Item should not be null");
        assertEquals(specificVersion, item.getVersion(), "Should create item with specific version");
        assertEquals(DEFAULT_PRODUCT_ID, item.getProductId(), "Should maintain default product ID");
        assertEquals(DEFAULT_STORE_ID, item.getStoreId(), "Should maintain default store ID");
    }

    @Test
    @DisplayName("Should handle minimum version value (zero)")
    void shouldHandleMinimumVersion() {
        // Arrange
        Long minVersion = 0L;

        // Act
        InventoryItem item = withVersion(minVersion);

        // Assert
        assertEquals(minVersion, item.getVersion(), "Should handle version zero correctly");
        assertTrue(item.getVersion() >= 0, "Version should be non-negative");
    }

    @Test
    @DisplayName("Should handle maximum version value")
    void shouldHandleMaximumVersion() {
        // Arrange
        Long maxVersion = Long.MAX_VALUE;

        // Act
        InventoryItem item = withVersion(maxVersion);

        // Assert
        assertEquals(maxVersion, item.getVersion(), "Should handle maximum version value");
        assertNotNull(item, "Item should be created successfully even with max version");
    }

    @Test
    @DisplayName("Should maintain other field values when changing version")
    void shouldMaintainOtherFieldsWhenChangingVersion() {
        // Arrange
        Long newVersion = 10L;

        // Act
        InventoryItem item = withVersion(newVersion);

        // Assert
        assertEquals(newVersion, item.getVersion(), "Version should be updated");
        assertEquals(DEFAULT_ID, item.getId(), "ID should remain unchanged");
        assertEquals(DEFAULT_CURRENT_STOCK, item.getCurrentStock(), "Current stock should remain unchanged");
        assertEquals(DEFAULT_RESERVED_STOCK, item.getReservedStock(), "Reserved stock should remain unchanged");
        assertEquals(DEFAULT_MIN_STOCK, item.getMinimumStockLevel(), "Minimum stock level should remain unchanged");
        assertEquals(DEFAULT_MAX_STOCK, item.getMaximumStockLevel(), "Maximum stock level should remain unchanged");
    }

    @Test
    @DisplayName("Should work with negative version for edge case testing")
    void shouldWorkWithNegativeVersionForEdgeCaseTesting() {
        // Arrange
        Long negativeVersion = -1L;

        // Act
        InventoryItem item = withVersion(negativeVersion);

        // Assert
        assertEquals(negativeVersion, item.getVersion(), "Should handle negative versions for edge case testing");
    }

}
