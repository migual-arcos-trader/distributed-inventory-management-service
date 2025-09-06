package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother;
import com.meli.distributed_inventory_management_service.infrastructure.config.database.TestContainersConfig;
import com.meli.distributed_inventory_management_service.infrastructure.config.database.TestPersistenceMapperConfig;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntityMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import({TestContainersConfig.class, SpringDataInventoryRepository.class, TestPersistenceMapperConfig.class})
@ActiveProfiles("testcontainers")
class SpringDataInventoryRepositoryIntegrationTest {

    @Autowired
    private SpringDataInventoryRepository repository;

    @Autowired
    private ReactiveInventoryJpaRepository jpaRepository;

    @Autowired
    private DatabaseClient databaseClient;

    private InventoryEntity testEntity;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll().block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));

        String insertSql = """
                 INSERT INTO inventory_items\s
                 (id, product_id, store_id, current_stock, reserved_stock,\s
                  minimum_stock_level, maximum_stock_level, last_updated, version,\s
                  created_at, updated_at)
                 VALUES\s
                 (:id, :productId, :storeId, :currentStock, :reservedStock,
                  :minimumStockLevel, :maximumStockLevel, :lastUpdated, :version,
                  :createdAt, :updatedAt)
                \s""";

        testEntity = InventoryEntityMother.createDefaultTestEntity();

        databaseClient.sql(insertSql)
                .bind("id", testEntity.getId())
                .bind("productId", testEntity.getProductId())
                .bind("storeId", testEntity.getStoreId())
                .bind("currentStock", testEntity.getCurrentStock())
                .bind("reservedStock", testEntity.getReservedStock())
                .bind("minimumStockLevel", testEntity.getMinimumStockLevel())
                .bind("maximumStockLevel", testEntity.getMaximumStockLevel())
                .bind("lastUpdated", testEntity.getLastUpdated())
                .bind("version", testEntity.getVersion())
                .bind("createdAt", testEntity.getCreatedAt())
                .bind("updatedAt", testEntity.getUpdatedAt())
                .fetch()
                .rowsUpdated()
                .block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));

        testEntity = jpaRepository.findById(IntegrationTestsConstants.TEST_ITEM_ID)
                .block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));
    }

    @Test
    @DisplayName("Should find inventory item by ID successfully in real database")
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
                    assertEquals(testEntity.getVersion(), item.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find inventory item by product and store in real database")
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
    @DisplayName("Should save new inventory item successfully in real database")
    void shouldSaveNewItemSuccessfully() {
        // Arrange
        InventoryItem newItem = InventoryItemMother.createNewItem();

        // Act
        Mono<InventoryItem> result = repository.save(newItem);

        // Assert
        StepVerifier.create(result)
                .assertNext(savedItem -> {
                    assertNotNull(savedItem);
                    assertEquals(newItem.getId(), savedItem.getId());
                    assertEquals(newItem.getProductId(), savedItem.getProductId());
                    assertEquals(IntegrationTestsConstants.NEW_ITEM_STOCK, savedItem.getCurrentStock());
                    assertNotNull(savedItem.getVersion());
                    assertTrue(savedItem.getVersion() >= IntegrationTestsConstants.ZERO_VERSION);
                })
                .verifyComplete();

        // Verify it's actually persisted
        StepVerifier.create(repository.findById(newItem.getId()))
                .assertNext(item -> assertEquals(newItem.getId(), item.getId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update inventory item with version check successfully")
    void shouldUpdateWithVersionCheckSuccessfully() {
        // Arrange
        InventoryItem existingItem = repository.findById(testEntity.getId())
                .block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));
        assertNotNull(existingItem, "El item debería existir en la base de datos");

        InventoryItem updatedItem = InventoryItemMother.createUpdatedItem(existingItem);

        // Act
        Mono<InventoryItem> result = repository.save(updatedItem);

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(IntegrationTestsConstants.UPDATED_STOCK, item.getCurrentStock());
                    assertEquals(existingItem.getVersion() + IntegrationTestsConstants.INITIAL_VERSION, item.getVersion());
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
                .currentStock(IntegrationTestsConstants.UPDATED_STOCK)
                .reservedStock(testEntity.getReservedStock())
                .minimumStockLevel(testEntity.getMinimumStockLevel())
                .maximumStockLevel(testEntity.getMaximumStockLevel())
                .lastUpdated(testEntity.getLastUpdated())
                .version(testEntity.getVersion() + IntegrationTestsConstants.INITIAL_VERSION)
                .build();

        Long wrongExpectedVersion = testEntity.getVersion() + IntegrationTestsConstants.WRONG_VERSION_OFFSET;

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
        // Arrange
        InventoryEntity anotherEntity = InventoryEntityMother.createEntityForStore(testEntity.getStoreId());
        jpaRepository.save(anotherEntity).block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));

        // Act
        Flux<InventoryItem> result = repository.findByStore(testEntity.getStoreId());

        // Assert
        StepVerifier.create(result)
                .expectNextCount(2)
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
        // Arrange
        InventoryEntity lowStockEntity = InventoryEntityMother.createLowStockEntity();
        String insertSql = """
                 INSERT INTO inventory_items\s
                 (id, product_id, store_id, current_stock, reserved_stock,\s
                  minimum_stock_level, maximum_stock_level, last_updated, version,\s
                  created_at, updated_at)
                 VALUES\s
                 (:id, :productId, :storeId, :currentStock, :reservedStock,
                  :minimumStockLevel, :maximumStockLevel, :lastUpdated, :version,
                  :createdAt, :updatedAt)
                \s""";

        databaseClient.sql(insertSql)
                .bind("id", lowStockEntity.getId())
                .bind("productId", lowStockEntity.getProductId())
                .bind("storeId", lowStockEntity.getStoreId())
                .bind("currentStock", lowStockEntity.getCurrentStock())
                .bind("reservedStock", lowStockEntity.getReservedStock())
                .bind("minimumStockLevel", lowStockEntity.getMinimumStockLevel())
                .bind("maximumStockLevel", lowStockEntity.getMaximumStockLevel())
                .bind("lastUpdated", lowStockEntity.getLastUpdated())
                .bind("version", lowStockEntity.getVersion())
                .bind("createdAt", lowStockEntity.getCreatedAt())
                .bind("updatedAt", lowStockEntity.getUpdatedAt())
                .fetch()
                .rowsUpdated()
                .block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));

        // Act
        Flux<InventoryItem> result = repository.findLowStockItems(IntegrationTestsConstants.TEST_STORE_ID, IntegrationTestsConstants.LOW_STOCK_THRESHOLD);

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(IntegrationTestsConstants.LOW_STOCK_ITEM_ID, item.getId());
                    assertTrue(item.getCurrentStock() - item.getReservedStock() < IntegrationTestsConstants.LOW_STOCK_THRESHOLD);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle concurrent updates with optimistic locking")
    void shouldHandleConcurrentUpdates() {
        // Arrange
        Mono<InventoryItem> item1 = repository.findById(testEntity.getId());
        Mono<InventoryItem> item2 = repository.findById(testEntity.getId());

        // Act
        Mono<InventoryItem> result1 = item1
                .subscribeOn(Schedulers.parallel())
                .flatMap(i -> {
                    InventoryItem updated = InventoryItem.builder()
                            .id(i.getId())
                            .productId(i.getProductId())
                            .storeId(i.getStoreId())
                            .currentStock(i.getCurrentStock() + 10)
                            .reservedStock(i.getReservedStock())
                            .minimumStockLevel(i.getMinimumStockLevel())
                            .maximumStockLevel(i.getMaximumStockLevel())
                            .lastUpdated(java.time.LocalDateTime.now())
                            .version(i.getVersion())
                            .build();
                    return repository.save(updated);
                });

        Mono<InventoryItem> result2 = item2
                .subscribeOn(Schedulers.parallel())
                .flatMap(i -> {
                    InventoryItem updated = InventoryItem.builder()
                            .id(i.getId())
                            .productId(i.getProductId())
                            .storeId(i.getStoreId())
                            .currentStock(i.getCurrentStock() + 20)
                            .reservedStock(i.getReservedStock())
                            .minimumStockLevel(i.getMinimumStockLevel())
                            .maximumStockLevel(i.getMaximumStockLevel())
                            .lastUpdated(java.time.LocalDateTime.now())
                            .version(i.getVersion())
                            .build();
                    return repository.save(updated);
                });

        // Assert
        Flux<Object> combinedOperations = Flux.merge(
                result1.map(item -> (Object) item)
                        .onErrorResume(Mono::just),
                result2.map(item -> (Object) item)
                        .onErrorResume(Mono::just)
        );

        StepVerifier.create(combinedOperations.collectList())
                .assertNext(results -> {
                    assertEquals(2, results.size(), "Deberían haber 2 resultados");

                    long successCount = results.stream()
                            .filter(r -> r instanceof InventoryItem)
                            .count();

                    long errorCount = results.stream()
                            .filter(r -> r instanceof OptimisticLockingFailureException)
                            .count();

                    assertEquals(IntegrationTestsConstants.UNIT_EXPECT, successCount, "Debería haber exactamente 1 operación exitosa");
                    assertEquals(IntegrationTestsConstants.UNIT_EXPECT, errorCount, "Debería haber exactamente 1 operación fallida");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update stock with version successfully")
    void shouldUpdateStockWithVersionSuccessfully() {
        // Act
        Mono<Integer> result = repository.updateStockWithVersion(
                testEntity.getId(),
                IntegrationTestsConstants.UPDATED_STOCK,
                testEntity.getVersion()
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(IntegrationTestsConstants.UNIT_EXPECT)
                .verifyComplete();

        // Verifica que el stock se actualizó y la versión incrementó
        StepVerifier.create(repository.findById(testEntity.getId()))
                .assertNext(item -> {
                    assertEquals(IntegrationTestsConstants.UPDATED_STOCK, item.getCurrentStock());
                    assertEquals(testEntity.getVersion() + IntegrationTestsConstants.UNIT_EXPECT, item.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 0 rows when updateStockWithVersion has version mismatch")
    void shouldReturnZeroWhenUpdateStockVersionMismatch() {
        // Arrange
        Long wrongVersion = testEntity.getVersion() + IntegrationTestsConstants.WRONG_VERSION_OFFSET;

        // Act
        Mono<Integer> result = repository.updateStockWithVersion(
                testEntity.getId(),
                IntegrationTestsConstants.UPDATED_STOCK,
                wrongVersion
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(Math.toIntExact(IntegrationTestsConstants.ZERO_VERSION))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should reserve stock with version successfully")
    void shouldReserveStockWithVersionSuccessfully() {
        // Arrange
        int quantityToReserve = IntegrationTestsConstants.QUANTITY_TO_RESERVE;

        // Act
        Mono<Integer> result = repository.reserveStockWithVersion(
                testEntity.getId(),
                quantityToReserve,
                testEntity.getVersion()
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(IntegrationTestsConstants.UNIT_EXPECT)
                .verifyComplete();

        // Verifica que el stock reservado aumentó y la versión incrementó
        StepVerifier.create(repository.findById(testEntity.getId()))
                .assertNext(item -> {
                    assertEquals(IntegrationTestsConstants.INITIAL_CURRENT_STOCK, item.getCurrentStock());
                    assertEquals(IntegrationTestsConstants.INITIAL_RESERVED_STOCK + IntegrationTestsConstants.QUANTITY_TO_RESERVE, item.getReservedStock());
                    assertEquals(testEntity.getVersion() + IntegrationTestsConstants.UNIT_EXPECT, item.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should release reserved stock with version successfully")
    void shouldReleaseReservedStockWithVersionSuccessfully() {
        // Arrange
        int quantityToRelease = IntegrationTestsConstants.QUANTITY_TO_RELEASE;

        // Act
        Mono<Integer> result = repository.releaseReservedStockWithVersion(
                testEntity.getId(),
                quantityToRelease,
                testEntity.getVersion()
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(IntegrationTestsConstants.UNIT_EXPECT)
                .verifyComplete();

        // Verifica que el stock reservado disminuyó y la versión incrementó
        StepVerifier.create(repository.findById(testEntity.getId()))
                .assertNext(item -> {
                    assertEquals(IntegrationTestsConstants.INITIAL_CURRENT_STOCK, item.getCurrentStock());
                    assertEquals(IntegrationTestsConstants.INITIAL_RESERVED_STOCK - IntegrationTestsConstants.QUANTITY_TO_RELEASE, item.getReservedStock());
                    assertEquals(testEntity.getVersion() + IntegrationTestsConstants.UNIT_EXPECT, item.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should count overstock items successfully")
    void shouldCountOverstockItemsSuccessfully() {
        // Arrange
        InventoryEntity overstockEntity = InventoryEntityMother.createOverstockEntity();
        String insertSql = """
                 INSERT INTO inventory_items\s
                 (id, product_id, store_id, current_stock, reserved_stock,\s
                  minimum_stock_level, maximum_stock_level, last_updated, version,\s
                  created_at, updated_at)
                 VALUES\s
                 (:id, :productId, :storeId, :currentStock, :reservedStock,
                  :minimumStockLevel, :maximumStockLevel, :lastUpdated, :version,
                  :createdAt, :updatedAt)
                \s""";

        databaseClient.sql(insertSql)
                .bind("id", overstockEntity.getId())
                .bind("productId", overstockEntity.getProductId())
                .bind("storeId", overstockEntity.getStoreId())
                .bind("currentStock", overstockEntity.getCurrentStock())
                .bind("reservedStock", overstockEntity.getReservedStock())
                .bind("minimumStockLevel", overstockEntity.getMinimumStockLevel())
                .bind("maximumStockLevel", overstockEntity.getMaximumStockLevel())
                .bind("lastUpdated", overstockEntity.getLastUpdated())
                .bind("version", overstockEntity.getVersion())
                .bind("createdAt", overstockEntity.getCreatedAt())
                .bind("updatedAt", overstockEntity.getUpdatedAt())
                .fetch()
                .rowsUpdated()
                .block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));

        // Act
        Mono<Integer> result = repository.countOverstockItems(IntegrationTestsConstants.TEST_STORE_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(IntegrationTestsConstants.UNIT_EXPECT)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 0 when no overstock items")
    void shouldReturnZeroWhenNoOverstockItems() {
        // Act
        Mono<Integer> result = repository.countOverstockItems(IntegrationTestsConstants.DIFFERENT_STORE_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(Math.toIntExact(IntegrationTestsConstants.ZERO_VERSION))
                .verifyComplete();
    }

}
