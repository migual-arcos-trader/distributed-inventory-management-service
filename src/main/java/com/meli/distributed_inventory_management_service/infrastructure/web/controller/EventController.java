package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.application.service.EventApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/inventory/events")
@RequiredArgsConstructor
public class EventController {

    private final EventApplicationService eventApplicationService;

    @GetMapping
    public Flux<EventResponseDTO> getAllEvents() {
        return eventApplicationService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    public Mono<ResponseEntity<EventResponseDTO>> getEventById(
            @PathVariable String eventId) {

        return eventApplicationService.getEventById(eventId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public Flux<EventResponseDTO> getEventsByStatus(
            @PathVariable String status) {

        return eventApplicationService.getEventsByStatus(status);
    }

    @GetMapping("/correlation/{correlationId}")
    public Flux<EventResponseDTO> getEventsByCorrelationId(
            @PathVariable String correlationId) {

        return eventApplicationService.getEventsByCorrelationId(correlationId);
    }

    @PostMapping("/{eventId}/compensate")
    public Mono<ResponseEntity<EventResponseDTO>> compensateEvent(
            @PathVariable String eventId,
            @RequestParam String reason) {

        return eventApplicationService.compensateEvent(eventId, reason)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalStateException.class,
                        error -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.internalServerError().build()));
    }
}