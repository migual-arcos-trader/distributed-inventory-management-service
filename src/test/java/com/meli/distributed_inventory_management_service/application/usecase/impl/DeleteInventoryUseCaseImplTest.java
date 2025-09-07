package com.meli.distributed_inventory_management_service.application.usecase.impl;

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
class DeleteInventoryUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.InventoryRepositoryPort inventoryRepositoryPort;

    @InjectMocks
    private DeleteInventoryUseCaseImpl deleteInventoryUseCase;

    @Test
    @DisplayName("Should delete inventory successfully")
    void shouldDeleteInventorySuccessfully() {
        // Arrange
        when(inventoryRepositoryPort.deleteById(anyString()))
                .thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = deleteInventoryUseCase.execute("test-id");

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when inventory not found for deletion")
    void shouldReturnFalseWhenInventoryNotFound() {
        // Arrange
        when(inventoryRepositoryPort.deleteById(anyString()))
                .thenReturn(Mono.just(false));

        // Act
        Mono<Boolean> result = deleteInventoryUseCase.execute("non-existent-id");

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}