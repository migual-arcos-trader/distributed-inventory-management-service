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
class GetInventoryByStoreUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort inventoryRepositoryPort;

    @InjectMocks
    private GetInventoryByStoreUseCaseImpl getInventoryByStoreUseCase;

    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should get inventory by store successfully")
    void shouldGetInventoryByStoreSuccessfully() {
        // Arrange
        when(inventoryRepositoryPort.findByStoreId(anyString()))
                .thenReturn(Flux.just(inventoryItem));

        // Act
        Flux<InventoryItem> result = getInventoryByStoreUseCase.execute(ApplicationTestConstants.STORE_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }
}