package com.meli.distributed_inventory_management_service.domain.service;

import com.meli.distributed_inventory_management_service.domain.exception.ConcurrentUpdateException;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import com.meli.distributed_inventory_management_service.domain.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryItem existingItem;

    @BeforeEach
    void setUp() {
        existingItem = basicItem();
    }

    @Test
    @DisplayName("Should successfully update stock when item exists")
    void shouldUpdateStockWhenItemExists() {
        // Arrange
        when(inventoryRepository.findByProductAndStore(any(), any()))
                .thenReturn(Mono.just(existingItem));
        when(inventoryRepository.updateWithVersionCheck(any(), any()))
                .thenReturn(Mono.just(existingItem));

        // Act
        Mono<InventoryItem> result = inventoryService.updateStockWithRetry(
                "prod-1", "store-1", 50, UpdateType.PURCHASE);

        // Assert
        StepVerifier.create(result)
                .expectNext(existingItem)
                .verifyComplete();

        verify(inventoryRepository).findByProductAndStore("prod-1", "store-1");
        verify(inventoryRepository).updateWithVersionCheck(any(), eq(1L));
    }

    @Test
    @DisplayName("Should create new item when item does not exist")
    void shouldCreateNewItemWhenNotExists() {
        // Arrange
        when(inventoryRepository.findByProductAndStore(any(), any()))
                .thenReturn(Mono.empty());
        when(inventoryRepository.save(any()))
                .thenReturn(Mono.just(existingItem));

        // Act
        Mono<InventoryItem> result = inventoryService.updateStockWithRetry(
                "prod-1", "store-1", 50, UpdateType.PURCHASE);

        // Assert
        StepVerifier.create(result)
                .expectNext(existingItem)
                .verifyComplete();

        verify(inventoryRepository).save(any(InventoryItem.class));
    }

    @Test
    @DisplayName("Should convert OptimisticLockingFailureException to ConcurrentUpdateException")
    void shouldConvertOptimisticLockingException() {
        // Arrange
        when(inventoryRepository.findByProductAndStore(any(), any()))
                .thenReturn(Mono.just(existingItem));
        when(inventoryRepository.updateWithVersionCheck(any(), any()))
                .thenReturn(Mono.error(new OptimisticLockingFailureException("Concurrent update")));

        // Act
        Mono<InventoryItem> result = inventoryService.updateStockWithRetry(
                "prod-1", "store-1", 50, UpdateType.PURCHASE);

        // Assert
        StepVerifier.create(result)
                .expectError(ConcurrentUpdateException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get available stock when item exists")
    void shouldGetAvailableStockWhenItemExists() {
        // Arrange
        when(inventoryRepository.findByProductAndStore(any(), any()))
                .thenReturn(Mono.just(existingItem));

        // Act
        Mono<Integer> result = inventoryService.getAvailableStock("prod-1", "store-1");

        // Assert
        StepVerifier.create(result)
                .expectNext(90) // 100 current - 10 reserved
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return zero available stock when item does not exist")
    void shouldReturnZeroAvailableStockWhenItemNotExists() {
        // Arrange
        when(inventoryRepository.findByProductAndStore(any(), any()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Integer> result = inventoryService.getAvailableStock("prod-1", "store-1");

        // Assert
        StepVerifier.create(result)
                .expectNext(0)
                .verifyComplete();
    }

}
