package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
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
class CheckInventoryExistsUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort inventoryRepositoryPort;

    @InjectMocks
    private CheckInventoryExistsUseCaseImpl checkInventoryExistsUseCase;

    @Test
    @DisplayName("Should return true when inventory exists")
    void shouldReturnTrueWhenInventoryExists() {
        // Arrange
        when(inventoryRepositoryPort.existsByProductIdAndStoreId(anyString(), anyString()))
                .thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = checkInventoryExistsUseCase.execute(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when inventory does not exist")
    void shouldReturnFalseWhenInventoryNotExists() {
        // Arrange
        when(inventoryRepositoryPort.existsByProductIdAndStoreId(anyString(), anyString()))
                .thenReturn(Mono.just(false));

        // Act
        Mono<Boolean> result = checkInventoryExistsUseCase.execute(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}