package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.application.service.ReservationApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/inventory/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationApplicationService reservationApplicationService;

    @PostMapping
    public Mono<ResponseEntity<ReservationResponseDTO>> createReservation(
            @Valid @RequestBody ReservationRequestDTO request) {

        return reservationApplicationService.createReservation(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(IllegalArgumentException.class,
                        error -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(IllegalStateException.class,
                        error -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @PatchMapping("/{reservationId}/confirm")
    public Mono<ResponseEntity<ReservationResponseDTO>> confirmReservation(
            @PathVariable String reservationId) {

        return reservationApplicationService.confirmReservation(reservationId)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class,
                        error -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class,
                        error -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @DeleteMapping("/{reservationId}")
    public Mono<ResponseEntity<Void>> releaseReservation(
            @PathVariable String reservationId) {

        return reservationApplicationService.releaseReservation(reservationId)
                .map(response -> ResponseEntity.noContent().<Void>build())
                .onErrorResume(IllegalArgumentException.class,
                        error -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class,
                        error -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping("/{reservationId}")
    public Mono<ResponseEntity<ReservationResponseDTO>> getReservationById(
            @PathVariable String reservationId) {

        return reservationApplicationService.getReservationById(reservationId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<ReservationResponseDTO> getAllReservations() {
        return reservationApplicationService.getAllReservations();
    }

    @GetMapping("/{reservationId}/validate")
    public Mono<ResponseEntity<Boolean>> validateReservation(
            @PathVariable String reservationId) {

        return reservationApplicationService.isReservationValid(reservationId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false));
    }
}