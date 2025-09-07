package com.meli.distributed_inventory_management_service.application.port.impl;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.application.mother.ApplicationInventoryMother;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.Reservation;
import com.meli.distributed_inventory_management_service.domain.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServicePortImplTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.ReservationRepositoryPort reservationRepositoryPort;

    @InjectMocks
    private ReservationServicePortImpl reservationServicePort;

    private Reservation validReservation;
    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        validReservation = ApplicationInventoryMother.createValidReservation();
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should generate valid reservation ID with prefix")
    void shouldGenerateValidReservationId() {
        // Act
        Mono<String> result = reservationServicePort.generateReservationId();

        // Assert
        StepVerifier.create(result)
                .assertNext(reservationId -> {
                    assert reservationId.startsWith(RESERVATION_PREFIX);
                    assert reservationId.length() > RESERVATION_PREFIX.length();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should reserve stock successfully")
    void shouldReserveStockSuccessfully() {
        // Arrange
        when(inventoryService.reserveStock(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(inventoryItem));

        // Act
        Mono<InventoryItem> result = reservationServicePort.reserveStock(
                ApplicationTestConstants.PRODUCT_ID, ApplicationTestConstants.STORE_ID, ApplicationTestConstants.QUANTITY);

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should release reservation successfully")
    void shouldReleaseReservationSuccessfully() {
        // Arrange
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(validReservation));
        when(inventoryService.releaseReservedStock(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(inventoryItem));

        // Act
        Mono<InventoryItem> result = reservationServicePort.releaseReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(inventoryItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error when releasing non-existent reservation")
    void shouldReturnErrorWhenReleasingNonExistentReservation() {
        // Arrange
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<InventoryItem> result = reservationServicePort.releaseReservation(ApplicationTestConstants.NON_EXISTENT_ID);

        // Assert
        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    @DisplayName("Should validate reservation as true for valid reservation")
    void shouldValidateReservationAsTrueForValidReservation() {
        // Arrange
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(validReservation));

        // Act
        Mono<Boolean> result = reservationServicePort.isReservationValid(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate reservation as false for expired reservation")
    void shouldValidateReservationAsFalseForExpiredReservation() {
        // Arrange
        Reservation expiredReservation = ApplicationInventoryMother.createExpiredReservation();
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(expiredReservation));

        // Act
        Mono<Boolean> result = reservationServicePort.isReservationValid(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate reservation as false for non-existent reservation")
    void shouldValidateReservationAsFalseForNonExistentReservation() {
        // Arrange
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Boolean> result = reservationServicePort.isReservationValid(ApplicationTestConstants.NON_EXISTENT_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}