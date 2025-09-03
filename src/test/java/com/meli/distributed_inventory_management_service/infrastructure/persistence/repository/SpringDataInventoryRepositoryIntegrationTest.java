package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.infrastructure.config.TestDatabaseConfig;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.meli.distributed_inventory_management_service.infrastructure.persistence.repository.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import({TestDatabaseConfig.class, SpringDataInventoryRepository.class})
class SpringDataInventoryRepositoryIntegrationTest {

    @Autowired
    private SpringDataInventoryRepository repository;

    @Autowired
    private ReactiveInventoryJpaRepository jpaRepository;

    private InventoryEntity testEntity;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        jpaRepository.deleteAll().block(Duration.ofSeconds(5));
        testEntity = createInventoryEntity();
        jpaRepository.save(testEntity).block(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Should find inventory item by ID successfully")
    void shouldFindByIdSuccessfully() {
        // Act
        Mono<InventoryItem> result = repository.findById(testEntity.getId());

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertNotNull(item);
                    assertEquals(testEntity.getId(), item.getId());
                    assertEquals(testEntity.getProductId(), item.getProductId());
                    assertEquals(testEntity.getCurrentStock(), item.getCurrentStock());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when finding non-existent ID")
    void shouldReturnEmptyForNonExistentId() {
        // Act
        Mono<InventoryItem> result = repository.findById("non-existent-id");

        // Assert
        StepVerifier.create(result)
                .verifyComplete(); // Expects completion without any item
    }

    @Test
    @DisplayName("Should find inventory item by product and store successfully")
    void shouldFindByProductAndStoreSuccessfully() {
        // Act
        Mono<InventoryItem> result = repository.findByProductAndStore(
                testEntity.getProductId(),
                testEntity.getStoreId()
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertNotNull(item);
                    assertEquals(testEntity.getProductId(), item.getProductId());
                    assertEquals(testEntity.getStoreId(), item.getStoreId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should save new inventory item successfully")
    void shouldSaveNewItemSuccessfully() {
        // Arrange
        InventoryEntity newEntity = createInventoryEntity();
        newEntity.setId("new-item-1");
        newEntity.setProductId("new-prod-1");
        newEntity.setStoreId("new-store-1");

        InventoryItem newItem = InventoryItem.builder()
                .id(newEntity.getId())
                .productId(newEntity.getProductId())
                .storeId(newEntity.getStoreId())
                .currentStock(newEntity.getCurrentStock())
                .reservedStock(newEntity.getReservedStock())
                .minimumStockLevel(newEntity.getMinimumStockLevel())
                .maximumStockLevel(newEntity.getMaximumStockLevel())
                .lastUpdated(newEntity.getLastUpdated())
                .version(newEntity.getVersion())
                .build();

        // Act
        Mono<InventoryItem> result = repository.save(newItem);

        // Assert
        StepVerifier.create(result)
                .assertNext(savedItem -> {
                    assertNotNull(savedItem);
                    assertEquals(newItem.getId(), savedItem.getId());
                    assertEquals(newItem.getProductId(), savedItem.getProductId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update inventory item with version check successfully")
    void shouldUpdateWithVersionCheckSuccessfully() {
        // Arrange
        InventoryItem updatedItem = InventoryItem.builder()
                .id(testEntity.getId())
                .productId(testEntity.getProductId())
                .storeId(testEntity.getStoreId())
                .currentStock(150) // Updated stock
                .reservedStock(testEntity.getReservedStock())
                .minimumStockLevel(testEntity.getMinimumStockLevel())
                .maximumStockLevel(testEntity.getMaximumStockLevel())
                .lastUpdated(testEntity.getLastUpdated())
                .version(testEntity.getVersion() + 1)
                .build();

        // Act
        Mono<InventoryItem> result = repository.updateWithVersionCheck(updatedItem, testEntity.getVersion());

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(150, item.getCurrentStock());
                    assertEquals(testEntity.getVersion() + 1, item.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw OptimisticLockingFailureException when version mismatch")
    void shouldThrowExceptionWhenVersionMismatch() {
        // Arrange
        InventoryItem updatedItem = InventoryItem.builder()
                .id(testEntity.getId())
                .productId(testEntity.getProductId())
                .storeId(testEntity.getStoreId())
                .currentStock(150)
                .reservedStock(testEntity.getReservedStock())
                .minimumStockLevel(testEntity.getMinimumStockLevel())
                .maximumStockLevel(testEntity.getMaximumStockLevel())
                .lastUpdated(testEntity.getLastUpdated())
                .version(testEntity.getVersion() + 1)
                .build();

        // Use wrong expected version to cause mismatch
        Long wrongExpectedVersion = testEntity.getVersion() + 5;

        // Act
        Mono<InventoryItem> result = repository.updateWithVersionCheck(updatedItem, wrongExpectedVersion);

        // Assert
        StepVerifier.create(result)
                .expectError(OptimisticLockingFailureException.class)
                .verify();
    }

    @Test
    @DisplayName("Should find items by store ID successfully")
    void shouldFindByStoreIdSuccessfully() {
        // Arrange - Add another item for the same store
        InventoryEntity anotherEntity = createInventoryEntity();
        anotherEntity.setId("item-2");
        anotherEntity.setProductId("prod-2");
        anotherEntity.setStoreId(testEntity.getStoreId()); // Same store
        jpaRepository.save(anotherEntity).block(Duration.ofSeconds(5));

        // Act
        Flux<InventoryItem> result = repository.findByStore(testEntity.getStoreId());

        // Assert
        StepVerifier.create(result)
                .expectNextCount(2) // Should find both items
                .verifyComplete();
    }

    @Test
    @DisplayName("Should check existence by product and store successfully")
    void shouldCheckExistenceByProductAndStore() {
        // Act
        Mono<Boolean> result = repository.existsByProductAndStore(
                testEntity.getProductId(),
                testEntity.getStoreId()
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when checking existence for non-existent product and store")
    void shouldReturnFalseForNonExistentProductAndStore() {
        // Act
        Mono<Boolean> result = repository.existsByProductAndStore(
                "non-existent-prod",
                "non-existent-store"
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete inventory item successfully")
    void shouldDeleteItemSuccessfully() {
        // Act
        Mono<Boolean> result = repository.delete(testEntity.getId());

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        // Verify it's actually deleted
        StepVerifier.create(repository.findById(testEntity.getId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find low stock items successfully")
    void shouldFindLowStockItems() {
        // Arrange - Create a low stock item
        InventoryEntity lowStockEntity = createInventoryEntity();
        lowStockEntity.setId("low-stock-item");
        lowStockEntity.setProductId("low-prod");
        lowStockEntity.setStoreId("store-1");
        lowStockEntity.setCurrentStock(10);
        lowStockEntity.setReservedStock(5); // Available: 5
        jpaRepository.save(lowStockEntity).block(Duration.ofSeconds(5));

        // Act - Find items with available stock less than 10
        Flux<InventoryItem> result = repository.findLowStockItems("store-1", 10);

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals("low-stock-item", item.getId());
                    assertTrue(item.getCurrentStock() - item.getReservedStock() < 10);
                })
                .verifyComplete();
    }
}