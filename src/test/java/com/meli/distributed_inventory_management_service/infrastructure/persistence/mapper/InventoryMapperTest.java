package com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;

import static com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem;
import static com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper.EntityTestFactory.*;
import static com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper.MapperTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    private InventoryMapper inventoryMapper;

    @BeforeEach
    void setUp() {
        inventoryMapper = InventoryMapper.INSTANCE;
    }

    @Test
    @DisplayName("Should map domain to entity correctly")
    void shouldMapDomainToEntity() {
        // Arrange
        InventoryItem domain = basicItem();

        // Act
        InventoryEntity entity = inventoryMapper.toEntity(domain);

        // Assert
        assertNotNull(entity, "Entity should not be null");
        assertEquals(domain.getId(), entity.getId(), "ID should match");
        assertEquals(domain.getProductId(), entity.getProductId(), "Product ID should match");
        assertEquals(domain.getCurrentStock(), entity.getCurrentStock(), "Current stock should match");
        assertEquals(domain.getReservedStock(), entity.getReservedStock(), "Reserved stock should match");
        assertEquals(domain.getVersion(), entity.getVersion(), "Version should match");
    }

    @Test
    @DisplayName("Should map entity to domain correctly")
    void shouldMapEntityToDomain() {
        // Arrange
        InventoryEntity entity = createBasicInventoryEntity();

        // Act
        InventoryItem domain = inventoryMapper.toDomain(entity);

        // Assert
        assertNotNull(domain, "Domain should not be null");
        assertEquals(entity.getId(), domain.getId(), "ID should match");
        assertEquals(entity.getProductId(), domain.getProductId(), "Product ID should match");
        assertEquals(entity.getCurrentStock(), domain.getCurrentStock(), "Current stock should match");
        assertEquals(entity.getReservedStock(), domain.getReservedStock(), "Reserved stock should match");
        assertEquals(entity.getVersion(), domain.getVersion(), "Version should match");
        assertEquals(entity.getMinimumStockLevel(), domain.getMinimumStockLevel(), "Minimum stock level should match");
        assertEquals(entity.getMaximumStockLevel(), domain.getMaximumStockLevel(), "Maximum stock level should match");
    }

    @Test
    @DisplayName("Should successfully map with version check when versions match")
    void shouldMapWithVersionCheckWhenVersionsMatch() {
        // Arrange
        InventoryEntity entity = createInventoryEntityWithVersion(VERSION_5);

        // Act & Assert
        assertDoesNotThrow(() -> inventoryMapper.toDomainWithVersion(entity, VERSION_5),
                "Should not throw exception when versions match");
    }

    @Test
    @DisplayName("Should map all fields correctly between domain and entity")
    void shouldMapAllFieldsCorrectly() {
        // Arrange
        InventoryEntity entity = createBasicInventoryEntity();

        // Act
        InventoryItem domain = inventoryMapper.toDomain(entity);
        InventoryEntity mappedBackEntity = inventoryMapper.toEntity(domain);

        // Assert
        assertNotNull(domain, "Domain should not be null");
        assertNotNull(mappedBackEntity, "Mapped back entity should not be null");

        assertEquals(entity.getId(), mappedBackEntity.getId(), "ID should match after round-trip");
        assertEquals(entity.getProductId(), mappedBackEntity.getProductId(), "Product ID should match after round-trip");
        assertEquals(entity.getCurrentStock(), mappedBackEntity.getCurrentStock(), "Current stock should match after round-trip");
        assertEquals(entity.getReservedStock(), mappedBackEntity.getReservedStock(), "Reserved stock should match after round-trip");
        assertEquals(entity.getVersion(), mappedBackEntity.getVersion(), "Version should match after round-trip");
    }

    @Test
    @DisplayName("Should throw exception when version check fails")
    void shouldThrowExceptionWhenVersionMismatch() {
        // Arrange
        InventoryEntity entity = createInventoryEntityWithVersion(VERSION_5);

        // Act & Assert
        OptimisticLockingFailureException exception = assertThrows(
                OptimisticLockingFailureException.class,
                () -> inventoryMapper.toDomainWithVersion(entity, VERSION_10),
                "Should throw OptimisticLockingFailureException when versions mismatch"
        );

        assertTrue(exception.getMessage().contains(VERSION_MISMATCH_ERROR + VERSION_10 + ACTUAL_VERSION + VERSION_5),
                "Exception message should contain version details");
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void shouldReturnNullWhenEntityIsNull() {
        // Act
        InventoryItem result = inventoryMapper.toDomain(null);

        // Assert
        assertNull(result, "Should return null for null entity");
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void shouldReturnNullWhenDomainIsNull() {
        // Act
        InventoryEntity result = inventoryMapper.toEntity(null);

        // Assert
        assertNull(result, "Should return null for null domain");
    }

    @Test
    @DisplayName("Should throw exception when entity is null in version check")
    void shouldThrowExceptionWhenEntityIsNullInVersionCheck() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> inventoryMapper.toDomainWithVersion(null, 1L),
                "Should throw NullPointerException for null entity in version check");
    }

    @Test
    @DisplayName("Should throw exception when expected version is null in version check")
    void shouldThrowExceptionWhenExpectedVersionIsNullInVersionCheck() {
        // Arrange
        InventoryEntity entity = createBasicInventoryEntity();

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> inventoryMapper.toDomainWithVersion(entity, null),
                "Should throw NullPointerException for null expected version");
    }

    @Test
    @DisplayName("Should create minimal entity with custom ID and version")
    void shouldCreateMinimalEntityWithCustomIdAndVersion() {
        // Arrange
        String customId = "custom-id-123";
        Long customVersion = 99L;

        // Act
        InventoryEntity entity = createMinimalInventoryEntity(customId, customVersion);

        // Assert
        assertNotNull(entity, "Entity should not be null");
        assertEquals(customId, entity.getId(), "Should use custom ID");
        assertEquals(customVersion, entity.getVersion(), "Should use custom version");
        assertEquals(PRODUCT_ID, entity.getProductId(), "Should use default product ID");
        assertEquals(STORE_ID, entity.getStoreId(), "Should use default store ID");
        assertEquals(CURRENT_STOCK, entity.getCurrentStock(), "Should use default current stock");
        assertEquals(RESERVED_STOCK, entity.getReservedStock(), "Should use default reserved stock");
    }

    @Test
    @DisplayName("Should map minimal entity correctly to domain")
    void shouldMapMinimalEntityCorrectly() {
        // Arrange
        String customId = "minimal-item-1";
        Long customVersion = 42L;
        InventoryEntity minimalEntity = createMinimalInventoryEntity(customId, customVersion);

        // Act
        InventoryItem domain = inventoryMapper.toDomain(minimalEntity);

        // Assert
        assertNotNull(domain, "Domain should not be null");
        assertEquals(customId, domain.getId(), "ID should be mapped correctly");
        assertEquals(customVersion, domain.getVersion(), "Version should be mapped correctly");
        assertEquals(PRODUCT_ID, domain.getProductId(), "Product ID should be mapped correctly");
        assertEquals(STORE_ID, domain.getStoreId(), "Store ID should be mapped correctly");
        assertEquals(CURRENT_STOCK, domain.getCurrentStock(), "Current stock should be mapped correctly");
        assertEquals(RESERVED_STOCK, domain.getReservedStock(), "Reserved stock should be mapped correctly");
    }

    @Test
    @DisplayName("Should create multiple minimal entities with different IDs")
    void shouldCreateMultipleMinimalEntitiesWithDifferentIds() {
        // Arrange
        String firstId = "entity-001";
        String secondId = "entity-002";
        Long version = 1L;

        // Act
        InventoryEntity firstEntity = createMinimalInventoryEntity(firstId, version);
        InventoryEntity secondEntity = createMinimalInventoryEntity(secondId, version);

        // Assert
        assertNotNull(firstEntity, "First entity should not be null");
        assertNotNull(secondEntity, "Second entity should not be null");
        assertEquals(firstId, firstEntity.getId(), "First entity should have correct ID");
        assertEquals(secondId, secondEntity.getId(), "Second entity should have correct ID");
        assertNotEquals(firstEntity.getId(), secondEntity.getId(), "Entities should have different IDs");
        assertEquals(firstEntity.getProductId(), secondEntity.getProductId(), "Should share same product ID");
        assertEquals(firstEntity.getVersion(), secondEntity.getVersion(), "Should share same version");
    }

    @Test
    @DisplayName("Should create entity with null ID when null is passed (Lombok builder behavior)")
    void shouldCreateEntityWithNullIdWhenNullPassed() {
        // Arrange
        Long version = 1L;

        // Act
        InventoryEntity entity = createMinimalInventoryEntity(null, version);

        // Assert
        assertNotNull(entity, "Entity should be created");
        assertNull(entity.getId(), "ID should be null");
        assertEquals(version, entity.getVersion(), "Version should be set correctly");
    }

    @Test
    @DisplayName("Should create entity with null version when null is passed (Lombok builder behavior)")
    void shouldCreateEntityWithNullVersionWhenNullPassed() {
        // Arrange
        String id = "test-id";

        // Act
        InventoryEntity entity = createMinimalInventoryEntity(id, null);

        // Assert
        assertNotNull(entity, "Entity should be created");
        assertEquals(id, entity.getId(), "ID should be set correctly");
        assertNull(entity.getVersion(), "Version should be null");
    }

    @Test
    @DisplayName("Should demonstrate real Lombok builder behavior with null parameters")
    void shouldDemonstrateRealLombokBuilderBehavior() {
        // Act
        InventoryEntity entity = InventoryEntity.builder()
                .id(null)
                .productId(null)
                .storeId(null)
                .currentStock(null)
                .reservedStock(null)
                .version(null)
                .build();

        // Assert
        assertNotNull(entity, "Entity should be created");
        assertNull(entity.getId());
        assertNull(entity.getProductId());
        assertNull(entity.getVersion());
    }

}
