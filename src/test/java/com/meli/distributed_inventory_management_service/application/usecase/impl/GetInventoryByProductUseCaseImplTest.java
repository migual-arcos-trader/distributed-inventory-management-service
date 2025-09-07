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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetInventoryByProductUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort inventoryRepositoryPort;

    @InjectMocks
    private GetInventoryByProductUseCaseImpl getInventoryByProductUseCase;

    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should get inventory by product successfully")
    void shouldGetInventoryByProductSuccessfully() {
        // Arrange
        when(inventoryRepositoryPort.findByProductId(anyString()))
                .thenReturn(Flux.just(inventoryItem));

        // Act
        Flux<InventoryItem> result = getInventoryByProductUseCase.execute(ApplicationTestConstants.PRODUCT_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when no inventory found for product")
    void shouldReturnEmptyWhenNoInventoryFound() {
        // Arrange
        when(inventoryRepositoryPort.findByProductId(anyString()))
                .thenReturn(Flux.empty());

        // Act
        Flux<InventoryItem> result = getInventoryByProductUseCase.execute(ApplicationTestConstants.PRODUCT_ID);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}