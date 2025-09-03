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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.meli.distributed_inventory_management_service.infrastructure.persistence.repository.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import({TestDatabaseConfig.class, SpringDataInventoryRepository.class})
class SpringDataInventoryRepositoryNativeIntegrationTest {

    @Autowired
    private SpringDataInventoryRepository repository;

    @Autowired
    private ReactiveInventoryJpaRepository jpaRepository;

    private InventoryEntity testEntity;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll().block(Duration.ofSeconds(5));
        testEntity = createInventoryEntity();
        jpaRepository.save(testEntity).block(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Should update with version check using native query successfully")
    void shouldUpdateWithVersionCheckNativeSuccessfully() {
        // Arrange
        InventoryItem updatedItem = InventoryItem.builder()
                .id(testEntity.getId())
                .productId(testEntity.getProductId())
                .storeId(testEntity.getStoreId())
                .currentStock(200)
                .reservedStock(testEntity.getReservedStock())
                .minimumStockLevel(testEntity.getMinimumStockLevel())
                .maximumStockLevel(testEntity.getMaximumStockLevel())
                .lastUpdated(testEntity.getLastUpdated())
                .version(testEntity.getVersion() + 1)
                .build();

        // Act
        Mono<InventoryItem> result = repository.updateWithVersionCheckNative(
                updatedItem,
                testEntity.getVersion()
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(200, item.getCurrentStock());
                    assertEquals(testEntity.getVersion() + 1, item.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw exception when native update has version mismatch")
    void shouldThrowExceptionWhenNativeUpdateVersionMismatch() {
        // Arrange
        InventoryItem updatedItem = InventoryItem.builder()
                .id(testEntity.getId())
                .productId(testEntity.getProductId())
                .storeId(testEntity.getStoreId())
                .currentStock(200)
                .reservedStock(testEntity.getReservedStock())
                .minimumStockLevel(testEntity.getMinimumStockLevel())
                .maximumStockLevel(testEntity.getMaximumStockLevel())
                .lastUpdated(testEntity.getLastUpdated())
                .version(testEntity.getVersion() + 1)
                .build();

        // Use wrong expected version
        Long wrongVersion = testEntity.getVersion() + 5;

        // Act
        Mono<InventoryItem> result = repository.updateWithVersionCheckNative(
                updatedItem,
                wrongVersion
        );

        // Assert
        StepVerifier.create(result)
                .expectError(OptimisticLockingFailureException.class)
                .verify();
    }

    @Test
    @DisplayName("Should execute atomic update successfully")
    void shouldExecuteAtomicUpdateSuccessfully() {
        // Arrange
        InventoryItem updatedItem = InventoryItem.builder()
                .id(testEntity.getId())
                .productId(testEntity.getProductId())
                .storeId(testEntity.getStoreId())
                .currentStock(250)
                .reservedStock(testEntity.getReservedStock())
                .minimumStockLevel(testEntity.getMinimumStockLevel())
                .maximumStockLevel(testEntity.getMaximumStockLevel())
                .lastUpdated(testEntity.getLastUpdated())
                .version(testEntity.getVersion() + 1)
                .build();

        // Act
        Mono<InventoryItem> result = repository.atomicUpdate(
                updatedItem,
                testEntity.getVersion()
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(250, item.getCurrentStock());
                    assertEquals(testEntity.getVersion() + 1, item.getVersion());
                })
                .verifyComplete();
    }

}
