package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother;
import com.meli.distributed_inventory_management_service.infrastructure.config.database.TestContainersConfig;
import com.meli.distributed_inventory_management_service.infrastructure.config.database.TestMapperConfig;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntityMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataR2dbcTest
@Import({TestContainersConfig.class, SpringDataInventoryRepository.class, TestMapperConfig.class})
@ActiveProfiles("testcontainers")
class SpringDataInventoryRepositoryNativeIntegrationTest {

    @Autowired
    private SpringDataInventoryRepository repository;

    @Autowired
    private ReactiveInventoryJpaRepository jpaRepository;

    private InventoryEntity testEntity;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll().block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));

        testEntity = InventoryEntityMother.createNativeTestEntity();
        jpaRepository.save(testEntity).block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));

        testEntity = jpaRepository.findById(IntegrationTestsConstants.NATIVE_TEST_ITEM_ID)
                .block(Duration.ofSeconds(IntegrationTestsConstants.TEST_TIMEOUT_SECONDS));
    }

    @Test
    @DisplayName("Should update with version check using native query successfully")
    void shouldUpdateWithVersionCheckNativeSuccessfully() {
        // Arrange
        InventoryItem updatedItem = InventoryItemMother.createNativeTestItem(testEntity);

        // Act
        Mono<InventoryItem> result = repository.updateWithVersionCheckNative(
                updatedItem,
                testEntity.getVersion()
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(IntegrationTestsConstants.NATIVE_UPDATED_STOCK, item.getCurrentStock());
                    assertEquals(testEntity.getVersion() + 1, item.getVersion());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw exception when native update has version mismatch")
    void shouldThrowExceptionWhenNativeUpdateVersionMismatch() {
        // Arrange
        InventoryItem updatedItem = InventoryItemMother.createNativeTestItem(testEntity);
        Long wrongVersion = testEntity.getVersion() + IntegrationTestsConstants.WRONG_VERSION_OFFSET;

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
        InventoryItem updatedItem = InventoryItemMother.createAtomicTestItem(testEntity);

        // Act
        Mono<InventoryItem> result = repository.atomicUpdate(
                updatedItem,
                testEntity.getVersion()
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(item -> {
                    assertEquals(IntegrationTestsConstants.ATOMIC_UPDATED_STOCK, item.getCurrentStock());
                    assertEquals(testEntity.getVersion() + 1, item.getVersion());
                })
                .verifyComplete();
    }
}
