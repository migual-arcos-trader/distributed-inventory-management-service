package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.constants.ApplicationTestConstants;
import com.meli.distributed_inventory_management_service.application.mother.ApplicationInventoryMother;
import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.usecase.EventUseCase;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import com.meli.distributed_inventory_management_service.domain.model.EventStatus;
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
class EventApplicationServiceTest {

    @Mock
    private EventUseCase eventUseCase;

    @InjectMocks
    private EventApplicationService eventApplicationService;

    private EventResponseDTO testEventResponse;

    @BeforeEach
    void setUp() {
        testEventResponse = new EventResponseDTO(
                ApplicationTestConstants.EVENT_ID,
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                ApplicationTestConstants.QUANTITY,
                com.meli.distributed_inventory_management_service.domain.model.UpdateType.PURCHASE,
                ApplicationTestConstants.EVENT_SOURCE,
                ApplicationTestConstants.CORRELATION_ID,
                java.time.LocalDateTime.now(),
                EventStatus.PENDING,
                null
        );
    }

    @Test
    @DisplayName("Should get event by ID successfully")
    void shouldGetEventByIdSuccessfully() {
        // Arrange
        when(eventUseCase.getEvent(anyString()))
                .thenReturn(Mono.just(testEventResponse));

        // Act
        Mono<EventResponseDTO> result = eventApplicationService.getEventById(ApplicationTestConstants.EVENT_ID);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(ApplicationTestConstants.EVENT_ID, response.eventId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get all events successfully")
    void shouldGetAllEventsSuccessfully() {
        // Arrange
        when(eventUseCase.getAllEvents())
                .thenReturn(Flux.just(testEventResponse));

        // Act
        Flux<EventResponseDTO> result = eventApplicationService.getAllEvents();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get events by status successfully")
    void shouldGetEventsByStatusSuccessfully() {
        // Arrange
        when(eventUseCase.getEventsByStatus(anyString()))
                .thenReturn(Flux.just(testEventResponse));

        // Act
        Flux<EventResponseDTO> result = eventApplicationService.getEventsByStatus(EventStatus.PENDING.name());

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get events by correlation ID successfully")
    void shouldGetEventsByCorrelationIdSuccessfully() {
        // Arrange
        when(eventUseCase.getEventsByCorrelationId(anyString()))
                .thenReturn(Flux.just(testEventResponse));

        // Act
        Flux<EventResponseDTO> result = eventApplicationService.getEventsByCorrelationId(ApplicationTestConstants.CORRELATION_ID);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should compensate event successfully")
    void shouldCompensateEventSuccessfully() {
        // Arrange
        EventResponseDTO compensatedResponse = new EventResponseDTO(
                ApplicationTestConstants.EVENT_ID,
                ApplicationTestConstants.PRODUCT_ID,
                ApplicationTestConstants.STORE_ID,
                ApplicationTestConstants.QUANTITY,
                com.meli.distributed_inventory_management_service.domain.model.UpdateType.PURCHASE,
                ApplicationTestConstants.EVENT_SOURCE,
                ApplicationTestConstants.CORRELATION_ID,
                java.time.LocalDateTime.now(),
                EventStatus.COMPENSATED,
                ApplicationTestConstants.COMPENSATION_REASON
        );

        when(eventUseCase.compensateEvent(anyString(), anyString()))
                .thenReturn(Mono.just(compensatedResponse));

        // Act
        Mono<EventResponseDTO> result = eventApplicationService.compensateEvent(
                ApplicationTestConstants.EVENT_ID,
                ApplicationTestConstants.COMPENSATION_REASON
        );

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> assertEquals(EventStatus.COMPENSATED, response.status()))
                .verifyComplete();
    }

}