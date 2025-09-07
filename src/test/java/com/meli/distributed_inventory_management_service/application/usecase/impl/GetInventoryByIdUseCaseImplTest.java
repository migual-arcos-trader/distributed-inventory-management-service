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
class GetInventoryByIdUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort inventoryRepositoryPort;

    @InjectMocks
    private GetInventoryByIdUseCaseImpl getInventoryByIdUseCase;

    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should get inventory by ID successfully")
    void shouldGetInventoryByIdSuccessfully() {
        // Arrange
        when(inventoryRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(inventoryItem));

        // Act
        Mono<InventoryItem> result = getInventoryByIdUseCase.execute("test-id");

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when inventory not found by ID")
    void shouldReturnEmptyWhenInventoryNotFound() {
        // Arrange
        when(inventoryRepositoryPort.findById(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<InventoryItem> result = getInventoryByIdUseCase.execute(ApplicationTestConstants.NON_EXISTENT_ID);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}