package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.infrastructure.config.TestContainersConfig;
import com.meli.distributed_inventory_management_service.infrastructure.config.TestMapperConfig;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import({TestContainersConfig.class, SpringDataInventoryRepository.class, TestMapperConfig.class})
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
        jpaRepository.deleteAll().block(Duration.ofSeconds(5));

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

        testEntity = InventoryEntity.builder()
                .id("integration-test-item")
                .productId("integration-prod-1")
                .storeId("integration-store-1")
                .currentStock(100)
                .reservedStock(10)
                .minimumStockLevel(5)
                .maximumStockLevel(200)
                .lastUpdated(java.time.LocalDateTime.now())
                .version(1L) // Versión inicial explícita
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

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
                .block(Duration.ofSeconds(5));

        testEntity = jpaRepository.findById("integration-test-item")
                .block(Duration.ofSeconds(5));
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
        String newId = "new-item-" + UUID.randomUUID();
        InventoryItem newItem = InventoryItem.builder()
                .id(newId)
                .productId("new-prod-1")
                .storeId("new-store-1")
                .currentStock(50)
                .reservedStock(5)
                .minimumStockLevel(2)
                .maximumStockLevel(100)
                .lastUpdated(java.time.LocalDateTime.now())
                .version(null)
                .build();

        // Act
        Mono<InventoryItem> result = repository.save(newItem);

        // Assert
        StepVerifier.create(result)
                .assertNext(savedItem -> {
                    assertNotNull(savedItem);
                    assertEquals(newId, savedItem.getId());
                    assertEquals("new-prod-1", savedItem.getProductId());
                    assertEquals(50, savedItem.getCurrentStock());
                    assertNotNull(savedItem.getVersion()); // La versión debería ser asignada por Spring Data
                    assertTrue(savedItem.getVersion() >= 0); // Generalmente 0 o 1
                })
                .verifyComplete();

        // Verify it's actually persisted
        StepVerifier.create(repository.findById(newId))
                .assertNext(item -> assertEquals(newId, item.getId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update inventory item with version check successfully")
    void shouldUpdateWithVersionCheckSuccessfully() {
        InventoryItem existingItem = repository.findById(testEntity.getId())
                .block(Duration.ofSeconds(5));

        assertNotNull(existingItem, "El item debería existir en la base de datos");

        // Arrange
        InventoryItem updatedItem = InventoryItem.builder()
                .id(existingItem.getId())
                .productId(existingItem.getProductId())
                .storeId(existingItem.getStoreId())
                .currentStock(150) // Updated stock
                .reservedStock(existingItem.getReservedStock())
                .minimumStockLevel(existingItem.getMinimumStockLevel())
                .maximumStockLevel(existingItem.getMaximumStockLevel())
                .lastUpdated(java.time.LocalDateTime.now())
                .version(existingItem.getVersion()) // ← Usa la versión ACTUAL del item real
                .build();

        // Act
        Mono<InventoryItem> result = repository.save(updatedItem);

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(150, item.getCurrentStock());
                    assertEquals(existingItem.getVersion() + 1, item.getVersion()); // La versión debería incrementarse
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
                .lastUpdated(java.time.LocalDateTime.now())
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
        InventoryEntity anotherEntity = InventoryEntity.builder()
                .id("item-store-2")
                .productId("prod-store-2")
                .storeId(testEntity.getStoreId()) // Same store
                .currentStock(75)
                .reservedStock(5)
                .minimumStockLevel(2)
                .maximumStockLevel(150)
                .lastUpdated(java.time.LocalDateTime.now())
                .version(null)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

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
        // Arrange - Create a low stock item using DatabaseClient (sin optimistic locking)
        InventoryEntity lowStockEntity = InventoryEntity.builder()
                .id("low-stock-item")
                .productId("low-prod")
                .storeId("store-1")
                .currentStock(10)
                .reservedStock(5) // Available: 5
                .minimumStockLevel(2)
                .maximumStockLevel(100)
                .lastUpdated(java.time.LocalDateTime.now())
                .version(1L) // Versión explícita
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        // Usa DatabaseClient para insertar sin verificación de versión
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
                .block(Duration.ofSeconds(5));

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

    @Test
    @DisplayName("Should handle concurrent updates with optimistic locking")
    void shouldHandleConcurrentUpdates() {
        // Arrange
        Mono<InventoryItem> item1 = repository.findById(testEntity.getId());
        Mono<InventoryItem> item2 = repository.findById(testEntity.getId());

        // Act - Crea las operaciones de actualización con concurrencia real
        Mono<InventoryItem> result1 = item1
                .subscribeOn(Schedulers.parallel()) // ← Ejecuta en hilo diferente
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
                .subscribeOn(Schedulers.parallel()) // ← Ejecuta en hilo diferente
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

        // Assert - Ejecuta AMBAS operaciones concurrentemente
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

                    assertEquals(1, successCount, "Debería haber exactamente 1 operación exitosa");
                    assertEquals(1, errorCount, "Debería haber exactamente 1 operación fallida");
                })
                .verifyComplete();
    }

}
