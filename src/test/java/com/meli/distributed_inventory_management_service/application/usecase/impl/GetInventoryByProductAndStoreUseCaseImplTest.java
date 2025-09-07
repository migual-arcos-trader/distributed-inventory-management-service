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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetInventoryByProductAndStoreUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort inventoryRepositoryPort;

    @InjectMocks
    private GetInventoryByProductAndStoreUseCaseImpl getInventoryByProductAndStoreUseCase;

    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should get inventory by product and store successfully")
    void shouldGetInventoryByProductAndStoreSuccessfully() {
        // Arrange
        when(inventoryRepositoryPort.findByProductIdAndStoreId(anyString(), anyString()))
                .thenReturn(Mono.just(inventoryItem));

        // Act
        Mono<InventoryItem> result = getInventoryByProductAndStoreUseCase.execute(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }
}