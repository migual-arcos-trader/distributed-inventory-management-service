package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationApplicationServiceTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.usecase.ReservationUseCase reservationUseCase;

    @InjectMocks
    private ReservationApplicationService reservationApplicationService;

    private ReservationResponseDTO testReservationResponse;
    private ReservationRequestDTO testReservationRequest;

    @BeforeEach
    void setUp() {
        testReservationResponse = new ReservationResponseDTO(
                ApplicationTestConstants.RESERVATION_ID,
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                ApplicationTestConstants.QUANTITY,
                "RESERVED",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusMinutes(30),
                ApplicationTestConstants.CORRELATION_ID
        );

        testReservationRequest = new ReservationRequestDTO(
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                ApplicationTestConstants.QUANTITY,
                ApplicationTestConstants.RESERVATION_ID,
                ApplicationTestConstants.CORRELATION_ID
        );
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void shouldCreateReservationSuccessfully() {
        // Arrange
        when(reservationUseCase.createReservation(anyString(), anyString(), any(), anyString()))
                .thenReturn(Mono.just(testReservationResponse));

        // Act
        Mono<ReservationResponseDTO> result = reservationApplicationService.createReservation(testReservationRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(ApplicationTestConstants.RESERVATION_ID, response.reservationId());
                    assertEquals(ApplicationTestConstants.PRODUCT_ID, response.productId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should confirm reservation successfully")
    void shouldConfirmReservationSuccessfully() {
        // Arrange
        ReservationResponseDTO confirmedResponse = new ReservationResponseDTO(
                ApplicationTestConstants.RESERVATION_ID,
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                ApplicationTestConstants.QUANTITY,
                "CONFIRMED",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusMinutes(30),
                ApplicationTestConstants.CORRELATION_ID
        );

        when(reservationUseCase.confirmReservation(anyString()))
                .thenReturn(Mono.just(confirmedResponse));

        // Act
        Mono<ReservationResponseDTO> result = reservationApplicationService.confirmReservation(
                ApplicationTestConstants.RESERVATION_ID
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals("CONFIRMED", response.status()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should release reservation successfully")
    void shouldReleaseReservationSuccessfully() {
        // Arrange
        ReservationResponseDTO releasedResponse = new ReservationResponseDTO(
                ApplicationTestConstants.RESERVATION_ID,
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                ApplicationTestConstants.QUANTITY,
                "RELEASED",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusMinutes(30),
                ApplicationTestConstants.CORRELATION_ID
        );

        when(reservationUseCase.releaseReservation(anyString()))
                .thenReturn(Mono.just(releasedResponse));

        // Act
        Mono<ReservationResponseDTO> result = reservationApplicationService.releaseReservation(
                ApplicationTestConstants.RESERVATION_ID
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals("RELEASED", response.status()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get reservation by ID successfully")
    void shouldGetReservationByIdSuccessfully() {
        // Arrange
        when(reservationUseCase.getReservation(anyString()))
                .thenReturn(Mono.just(testReservationResponse));

        // Act
        Mono<ReservationResponseDTO> result = reservationApplicationService.getReservationById(
                ApplicationTestConstants.RESERVATION_ID
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(ApplicationTestConstants.RESERVATION_ID, response.reservationId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get all reservations successfully")
    void shouldGetAllReservationsSuccessfully() {
        // Arrange
        when(reservationUseCase.getAllReservations())
                .thenReturn(Flux.just(testReservationResponse));

        // Act
        Flux<ReservationResponseDTO> result = reservationApplicationService.getAllReservations();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate reservation successfully")
    void shouldValidateReservationSuccessfully() {
        // Arrange
        when(reservationUseCase.validateReservation(anyString()))
                .thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = reservationApplicationService.isReservationValid(
                ApplicationTestConstants.RESERVATION_ID
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }
}