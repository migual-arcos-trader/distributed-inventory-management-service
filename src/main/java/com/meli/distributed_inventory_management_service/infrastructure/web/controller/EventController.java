package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.service.EventApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/inventory/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "APIs for managing inventory events and compensations")
public class EventController {

    private final EventApplicationService eventApplicationService;

    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieves a list of all inventory events")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved events list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EventResponseDTO.class)))
    public Flux<EventResponseDTO> getAllEvents() {
        return eventApplicationService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get event by ID", description = "Retrieves a specific event by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved event",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public Mono<ResponseEntity<EventResponseDTO>> getEventById(
            @Parameter(description = "ID of the event to retrieve", required = true, example = "event-12345")
            @PathVariable String eventId) {

        return eventApplicationService.getEventById(eventId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get events by status", description = "Retrieves all events with a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved events by status",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EventResponseDTO.class)))
    public Flux<EventResponseDTO> getEventsByStatus(
            @Parameter(description = "Status to filter events by", required = true, example = "COMPLETED")
            @PathVariable String status) {

        return eventApplicationService.getEventsByStatus(status);
    }

    @GetMapping("/correlation/{correlationId}")
    @Operation(summary = "Get events by correlation ID", description = "Retrieves all events with a specific correlation ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved events by correlation ID",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EventResponseDTO.class)))
    public Flux<EventResponseDTO> getEventsByCorrelationId(
            @Parameter(description = "Correlation ID to filter events by", required = true, example = "corr-67890")
            @PathVariable String correlationId) {

        return eventApplicationService.getEventsByCorrelationId(correlationId);
    }

    @PostMapping("/{eventId}/compensate")
    @Operation(summary = "Compensate event", description = "Compensates a specific event by providing a reason")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event successfully compensated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid compensation request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<EventResponseDTO>> compensateEvent(
            @Parameter(description = "ID of the event to compensate", required = true, example = "event-12345")
            @PathVariable String eventId,
            @Parameter(description = "Reason for compensation", required = true, example = "Stock discrepancy detected")
            @RequestParam String reason) {

        return eventApplicationService.compensateEvent(eventId, reason)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalStateException.class,
                        error -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.internalServerError().build()));
    }
}