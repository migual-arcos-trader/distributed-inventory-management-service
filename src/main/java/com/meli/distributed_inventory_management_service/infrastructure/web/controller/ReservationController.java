package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.application.service.ReservationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reservation Management", description = "APIs for managing inventory reservations")
public class ReservationController {

    private final ReservationApplicationService reservationApplicationService;

    @PostMapping
    @Operation(summary = "Create reservation", description = "Creates a new inventory reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict with existing reservation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<ReservationResponseDTO>> createReservation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Reservation details to create", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequestDTO.class)))
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
    @Operation(summary = "Confirm reservation", description = "Confirms a pending reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation successfully confirmed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "409", description = "Reservation cannot be confirmed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<ReservationResponseDTO>> confirmReservation(
            @Parameter(description = "ID of the reservation to confirm", required = true, example = "reserv-12345")
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
    @Operation(summary = "Release reservation", description = "Releases a reservation, making the stock available again")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reservation successfully released"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "409", description = "Reservation cannot be released"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<Void>> releaseReservation(
            @Parameter(description = "ID of the reservation to release", required = true, example = "reserv-12345")
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
    @Operation(summary = "Get reservation by ID", description = "Retrieves a specific reservation by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved reservation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public Mono<ResponseEntity<ReservationResponseDTO>> getReservationById(
            @Parameter(description = "ID of the reservation to retrieve", required = true, example = "reserv-12345")
            @PathVariable String reservationId) {

        return reservationApplicationService.getReservationById(reservationId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all reservations", description = "Retrieves a list of all reservations")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservations list",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReservationResponseDTO.class)))
    public Flux<ReservationResponseDTO> getAllReservations() {
        return reservationApplicationService.getAllReservations();
    }

    @GetMapping("/{reservationId}/validate")
    @Operation(summary = "Validate reservation", description = "Validates if a reservation is still valid and active")
    @ApiResponse(responseCode = "200", description = "Successfully validated reservation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    public Mono<ResponseEntity<Boolean>> validateReservation(
            @Parameter(description = "ID of the reservation to validate", required = true, example = "reserv-12345")
            @PathVariable String reservationId) {

        return reservationApplicationService.isReservationValid(reservationId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false));
    }
}