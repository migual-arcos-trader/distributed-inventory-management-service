package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.service.InventoryDomainService;
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
class UpdateStockUseCaseImplTest {

    @Mock
    private InventoryDomainService inventoryDomainService;

    @InjectMocks
    private UpdateStockUseCaseImpl updateStockUseCase;

    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should update stock successfully for purchase")
    void shouldUpdateStockSuccessfully() {
        // Arrange
        when(inventoryDomainService.updateStockWithRetry(anyString(), anyString(), any(), any()))
                .thenReturn(Mono.just(inventoryItem));

        // Act
        Mono<InventoryItem> result = updateStockUseCase.execute(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                10,
                "PURCHASE"
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error for invalid update type")
    void shouldReturnErrorForInvalidUpdateType() {
        // Act
        Mono<InventoryItem> result = updateStockUseCase.execute(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                10,
                "INVALID_TYPE"
        );

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}