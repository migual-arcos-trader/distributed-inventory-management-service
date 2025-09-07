package com.meli.distributed_inventory_management_service.application.usecase.impl;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.mother.ApplicationInventoryMother;
import com.meli.distributed_inventory_management_service.domain.model.EventStatus;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventUseCaseImplTest {

    @Mock
    private com.meli.distributed_inventory_management_service.application.port.EventRepositoryPort eventRepositoryPort;

    @InjectMocks
    private EventUseCaseImpl eventUseCase;

    private InventoryUpdateEvent testEvent;
    private InventoryUpdateEvent compensatableEvent;

    @BeforeEach
    void setUp() {
        testEvent = ApplicationInventoryMother.basicEvent().build();
        compensatableEvent = ApplicationInventoryMother.createCompensatableEvent();
    }

    @Test
    @DisplayName("Should get event by ID successfully")
    void shouldGetEventByIdSuccessfully() {
        // Arrange
        when(eventRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(testEvent));

        // Act
        Mono<EventResponseDTO> result = eventUseCase.getEvent(ApplicationTestConstants.EVENT_ID);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(ApplicationTestConstants.EVENT_ID, response.eventId());
                    assertEquals(ApplicationTestConstants.PRODUCT_ID, response.productId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when event not found by ID")
    void shouldReturnEmptyWhenEventNotFound() {
        // Arrange
        when(eventRepositoryPort.findById(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<EventResponseDTO> result = eventUseCase.getEvent(ApplicationTestConstants.NON_EXISTENT_ID);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get all events successfully")
    void shouldGetAllEventsSuccessfully() {
        // Arrange
        when(eventRepositoryPort.findAll())
                .thenReturn(Flux.just(testEvent));

        // Act
        Flux<EventResponseDTO> result = eventUseCase.getAllEvents();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get events by status successfully")
    void shouldGetEventsByStatusSuccessfully() {
        // Arrange
        when(eventRepositoryPort.findByStatus(anyString()))
                .thenReturn(Flux.just(testEvent));

        // Act
        Flux<EventResponseDTO> result = eventUseCase.getEventsByStatus(EventStatus.PENDING.name());

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should compensate event successfully")
    void shouldCompensateEventSuccessfully() {
        // Arrange
        InventoryUpdateEvent compensatedEvent = compensatableEvent.withStatus(EventStatus.COMPENSATED);
        when(eventRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(compensatableEvent));
        when(eventRepositoryPort.updateStatus(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(compensatedEvent));

        // Act
        Mono<EventResponseDTO> result = eventUseCase.compensateEvent(
                ApplicationTestConstants.EVENT_ID,
                ApplicationTestConstants.COMPENSATION_REASON
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(EventStatus.COMPENSATED.name(), response.status().name()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error when compensating non-compensatable event")
    void shouldReturnErrorWhenCompensatingNonCompensatableEvent() {
        // Arrange
        InventoryUpdateEvent nonCompensatableEvent = ApplicationInventoryMother.createNonCompensatableEvent();
        when(eventRepositoryPort.findById(anyString()))
                .thenReturn(Mono.just(nonCompensatableEvent));

        // Act
        Mono<EventResponseDTO> result = eventUseCase.compensateEvent(
                ApplicationTestConstants.EVENT_ID,
                ApplicationTestConstants.COMPENSATION_REASON
        );

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    @DisplayName("Should return error when compensating non-existent event")
    void shouldReturnErrorWhenCompensatingNonExistentEvent() {
        // Arrange
        when(eventRepositoryPort.findById(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<EventResponseDTO> result = eventUseCase.compensateEvent(
                ApplicationTestConstants.NON_EXISTENT_ID,
                ApplicationTestConstants.COMPENSATION_REASON
        );

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}