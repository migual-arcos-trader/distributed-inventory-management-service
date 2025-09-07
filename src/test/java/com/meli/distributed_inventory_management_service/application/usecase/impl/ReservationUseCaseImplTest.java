package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.application.mother.ApplicationInventoryMother;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.Reservation;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.ReservationServicePort reservationServicePort;

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.ReservationRepositoryPort reservationRepositoryPort;

    @InjectMocks
    private ReservationUseCaseImpl reservationUseCase;

    private Reservation validReservation;
    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        validReservation = ApplicationInventoryMother.createValidReservation();
        inventoryItem = com.meli.distributed_inventory_management_service.domain.model.InventoryItemMother.basicItem();
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void shouldCreateReservationSuccessfully() {
        // Arrange
        when(reservationServicePort.generateReservationId())
                .thenReturn(Mono.just(ApplicationTestConstants.RESERVATION_ID));
        when(reservationRepositoryPort.save(any(Reservation.class)))
                .thenReturn(Mono.just(validReservation));
        when(reservationServicePort.reserveStock(anyString(), anyString(), any()))
                .thenReturn(Mono.just(inventoryItem));
        when(reservationRepositoryPort.updateStatus(anyString(), anyString()))
                .thenReturn(Mono.just(validReservation));
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(validReservation));

        // Act
        Mono<ReservationResponseDTO> result = reservationUseCase.createReservation(
                ApplicationTestConstants.PRODUCT_ID, ApplicationTestConstants.STORE_ID, ApplicationTestConstants.QUANTITY, ApplicationTestConstants.CORRELATION_ID
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(ApplicationTestConstants.RESERVATION_ID, response.reservationId());
                    assertEquals(ApplicationTestConstants.PRODUCT_ID, response.productId());
                    assertEquals(STATUS_RESERVED, response.status());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error when confirming non-confirmable reservation")
    void shouldReturnErrorWhenConfirmingNonConfirmableReservation() {
        // Arrange
        Reservation nonConfirmableReservation = ApplicationInventoryMother.createReservationWithStatus(STATUS_PENDING);
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(nonConfirmableReservation));

        // Act
        Mono<ReservationResponseDTO> result = reservationUseCase.confirmReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    @DisplayName("Should confirm valid reservation")
    void shouldConfirmValidReservation() {
        // Arrange
        Reservation confirmableReservation = ApplicationInventoryMother.createReservationWithStatus(STATUS_RESERVED);
        Reservation confirmedReservation = confirmableReservation.confirm();

        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(confirmableReservation));
        when(reservationServicePort.confirmReservation(anyString()))
                .thenReturn(Mono.just(inventoryItem));
        when(reservationRepositoryPort.updateStatus(anyString(), anyString()))
                .thenReturn(Mono.just(confirmedReservation));
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(confirmedReservation));  // Este es el mock importante

        // Act
        Mono<ReservationResponseDTO> result = reservationUseCase.confirmReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(STATUS_CONFIRMED, response.status()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should release reservation successfully")
    void shouldReleaseReservationSuccessfully() {
        // Arrange
        Reservation releasableReservation = ApplicationInventoryMother.createReservationWithStatus(STATUS_RESERVED);
        Reservation releasedReservation = releasableReservation.release();

        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(releasableReservation));
        when(reservationServicePort.releaseReservation(anyString()))
                .thenReturn(Mono.just(inventoryItem));
        when(reservationRepositoryPort.updateStatus(anyString(), anyString()))
                .thenReturn(Mono.just(releasedReservation));
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(releasedReservation));

        // Act
        Mono<ReservationResponseDTO> result = reservationUseCase.releaseReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(STATUS_RELEASED, response.status()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate reservation as true for confirmable reservation")
    void shouldValidateReservationAsTrueForConfirmableReservation() {
        // Arrange
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(validReservation));

        // Act
        Mono<Boolean> result = reservationUseCase.validateReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate reservation as false for non-confirmable reservation")
    void shouldValidateReservationAsFalseForNonConfirmableReservation() {
        // Arrange
        Reservation nonConfirmable = ApplicationInventoryMother.createReservationWithStatus(STATUS_PENDING);
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(nonConfirmable));

        // Act
        Mono<Boolean> result = reservationUseCase.validateReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle error during reservation creation")
    void shouldHandleErrorDuringReservationCreation() {
        // Arrange
        when(reservationServicePort.generateReservationId())
                .thenReturn(Mono.just(ApplicationTestConstants.RESERVATION_ID));
        when(reservationRepositoryPort.save(any(Reservation.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO> result =
                reservationUseCase.createReservation(
                        ApplicationTestConstants.PRODUCT_ID,
                        ApplicationTestConstants.STORE_ID,
                        ApplicationTestConstants.QUANTITY,
                        ApplicationTestConstants.CORRELATION_ID
                );

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle error during stock reservation")
    void shouldHandleErrorDuringStockReservation() {
        // Arrange
        Reservation reservation = ApplicationInventoryMother.createValidReservation();
        when(reservationServicePort.generateReservationId())
                .thenReturn(Mono.just(ApplicationTestConstants.RESERVATION_ID));
        when(reservationRepositoryPort.save(any(Reservation.class)))
                .thenReturn(Mono.just(reservation));
        when(reservationServicePort.reserveStock(anyString(), anyString(), any()))
                .thenReturn(Mono.error(new RuntimeException("Stock reservation failed")));

        // Act
        Mono<com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO> result =
                reservationUseCase.createReservation(
                        ApplicationTestConstants.PRODUCT_ID,
                        ApplicationTestConstants.STORE_ID,
                        ApplicationTestConstants.QUANTITY,
                        ApplicationTestConstants.CORRELATION_ID
                );

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle expired reservation during confirmation")
    void shouldHandleExpiredReservationDuringConfirmation() {
        // Arrange
        Reservation expiredReservation = ApplicationInventoryMother.createExpiredReservation();
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(expiredReservation));

        // Act
        Mono<com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO> result =
                reservationUseCase.confirmReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle already released reservation")
    void shouldHandleAlreadyReleasedReservation() {
        // Arrange
        Reservation releasedReservation = ApplicationInventoryMother.createReservationWithStatus("RELEASED");
        when(reservationRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(releasedReservation));

        // Act
        Mono<com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO> result =
                reservationUseCase.releaseReservation(ApplicationTestConstants.RESERVATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }

}