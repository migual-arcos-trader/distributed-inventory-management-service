package com.meli.distributed_inventory_management_service.application.port.impl;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.application.mother.ApplicationInventoryMother;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import com.meli.distributed_inventory_management_service.domain.model.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryPortImplTest {

    private EventRepositoryPortImpl eventRepositoryPort;

    private InventoryUpdateEvent testEvent;

    @BeforeEach
    void setUp() {
        eventRepositoryPort = new EventRepositoryPortImpl();
        testEvent = ApplicationInventoryMother.basicEvent().build();
    }

    @Test
    @DisplayName("Should save event successfully")
    void shouldSaveEventSuccessfully() {
        // Act
        Mono<InventoryUpdateEvent> result = eventRepositoryPort.save(testEvent);

        // Assert
        StepVerifier.create(result)
                .assertNext(savedEvent -> {
                    assertEquals(testEvent.getEventId(), savedEvent.getEventId());
                    assertEquals(testEvent.getProductId(), savedEvent.getProductId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find event by ID")
    void shouldFindEventById() {
        // Arrange
        eventRepositoryPort.save(testEvent).block();

        // Act
        Mono<InventoryUpdateEvent> result = eventRepositoryPort.findById(testEvent.getEventId());

        // Assert
        StepVerifier.create(result)
                .assertNext(foundEvent -> {
                    assertEquals(testEvent.getEventId(), foundEvent.getEventId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when event not found by ID")
    void shouldReturnEmptyWhenEventNotFound() {
        // Act
        Mono<InventoryUpdateEvent> result = eventRepositoryPort.findById(ApplicationTestConstants.NON_EXISTENT_ID);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find all events")
    void shouldFindAllEvents() {
        // Arrange
        eventRepositoryPort.save(testEvent).block();
        InventoryUpdateEvent anotherEvent = ApplicationInventoryMother.basicEvent()
                .eventId("event-789")
                .build();
        eventRepositoryPort.save(anotherEvent).block();

        // Act
        Flux<InventoryUpdateEvent> result = eventRepositoryPort.findAll();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update event status successfully")
    void shouldUpdateEventStatusSuccessfully() {
        // Arrange
        eventRepositoryPort.save(testEvent).block();

        // Act
        Mono<InventoryUpdateEvent> result = eventRepositoryPort.updateStatus(
                testEvent.getEventId(),
                EventStatus.PROCESSED.name(),
                null  // Sin error details
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(updatedEvent -> {
                    assertEquals(EventStatus.PROCESSED, updatedEvent.getStatus());
                    assertNull(updatedEvent.getErrorDetails());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update event status with error details")
    void shouldUpdateEventStatusWithErrorDetails() {
        // Arrange
        eventRepositoryPort.save(testEvent).block();

        // Act
        Mono<InventoryUpdateEvent> result = eventRepositoryPort.updateStatus(
                testEvent.getEventId(),
                EventStatus.FAILED.name(),
                ApplicationTestConstants.ERROR_MESSAGE
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(updatedEvent -> {
                    assertEquals(EventStatus.FAILED, updatedEvent.getStatus());
                    assertEquals(ApplicationTestConstants.ERROR_MESSAGE, updatedEvent.getErrorDetails());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return error when updating non-existent event")
    void shouldReturnErrorWhenUpdatingNonExistentEvent() {
        // Act
        Mono<InventoryUpdateEvent> result = eventRepositoryPort.updateStatus(
                ApplicationTestConstants.NON_EXISTENT_ID,
                EventStatus.PROCESSED.name(),
                null
        );

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find events by correlation ID")
    void shouldFindEventsByCorrelationId() {
        // Arrange
        eventRepositoryPort.save(testEvent).block();
        InventoryUpdateEvent anotherEvent = ApplicationInventoryMother.basicEvent()
                .eventId("event-789")
                .correlationId("different-corr")
                .build();
        eventRepositoryPort.save(anotherEvent).block();

        // Act
        Flux<InventoryUpdateEvent> result = eventRepositoryPort.findByCorrelationId(ApplicationTestConstants.CORRELATION_ID);

        // Assert
        StepVerifier.create(result)
                .assertNext(event -> assertEquals(ApplicationTestConstants.CORRELATION_ID, event.getCorrelationId()))
                .verifyComplete();
    }
}