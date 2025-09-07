package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateInventoryUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort inventoryRepositoryPort;

    @InjectMocks
    private CreateInventoryUseCaseImpl createInventoryUseCase;

    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should create inventory successfully when not exists")
    void shouldCreateInventorySuccessfully() {
        // Arrange
        when(inventoryRepositoryPort.existsByProductIdAndStoreId(anyString(), anyString()))
                .thenReturn(Mono.just(false));
        when(inventoryRepositoryPort.save(any(InventoryItem.class)))
                .thenReturn(Mono.just(inventoryItem));

        // Act
        Mono<InventoryItem> result = createInventoryUseCase.execute(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                100
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error when inventory already exists")
    void shouldReturnErrorWhenInventoryExists() {
        // Arrange
        when(inventoryRepositoryPort.existsByProductIdAndStoreId(anyString(), anyString()))
                .thenReturn(Mono.just(true));

        // Act
        Mono<InventoryItem> result = createInventoryUseCase.execute(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                100
        );

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}