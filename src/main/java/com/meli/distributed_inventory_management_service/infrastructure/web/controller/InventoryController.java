package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import com.meli.distributed_inventory_management_service.domain.service.InventoryService;
import com.meli.distributed_inventory_management_service.infrastructure.web.dto.InventoryRequestDTO;
import com.meli.distributed_inventory_management_service.infrastructure.web.dto.InventoryResponseDTO;
import com.meli.distributed_inventory_management_service.infrastructure.web.dto.StockUpdateRequestDTO;
import com.meli.distributed_inventory_management_service.infrastructure.web.mapper.WebInventoryMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final WebInventoryMapper webInventoryMapper;

    @GetMapping
    public Flux<InventoryResponseDTO> getAllInventory() {
        // TODO: Implementar cuando el service tenga método para obtener todos
        return Flux.empty();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<InventoryResponseDTO>> getInventoryById(@PathVariable String id) {
        // TODO: Implementar cuando el service tenga método findById
        return Mono.just(ResponseEntity.notFound().build());
    }

    @GetMapping("/store/{storeId}")
    public Flux<InventoryResponseDTO> getInventoryByStore(@PathVariable String storeId) {
        // TODO: Implementar cuando el service tenga método findByStore
        return Flux.empty();
    }

    @GetMapping("/product/{productId}")
    public Flux<InventoryResponseDTO> getInventoryByProduct(@PathVariable String productId) {
        // TODO: Implementar cuando el service tenga método findByProduct
        return Flux.empty();
    }

    @PostMapping
    public Mono<ResponseEntity<InventoryResponseDTO>> createInventory(@Valid @RequestBody InventoryRequestDTO request) {
        // Crear item con stock inicial usando RESTOCK (establece stock exacto)
        return inventoryService.updateStockWithRetry(
                        request.productId(),
                        request.storeId(),
                        request.currentStock(),
                        UpdateType.RESTOCK
                )
                .map(webInventoryMapper::toResponseDTO)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @PutMapping("/stock")
    public Mono<ResponseEntity<InventoryResponseDTO>> updateStockByProductAndStore(
            @RequestParam String productId,
            @RequestParam String storeId,
            @Valid @RequestBody StockUpdateRequestDTO request) {

        return inventoryService.updateStockWithRetry(
                        productId,
                        storeId,
                        request.quantity(),
                        request.updateType()
                )
                .map(webInventoryMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    if (error instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/{productId}/{storeId}/reserve")
    public Mono<ResponseEntity<InventoryResponseDTO>> reserveStock(
            @PathVariable String productId,
            @PathVariable String storeId,
            @RequestParam @Valid @Positive Integer quantity) {

        return inventoryService.reserveStock(productId, storeId, quantity)
                .map(webInventoryMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/{productId}/{storeId}/release")
    public Mono<ResponseEntity<InventoryResponseDTO>> releaseReservedStock(
            @PathVariable String productId,
            @PathVariable String storeId,
            @RequestParam @Valid @Positive Integer quantity) {

        return inventoryService.releaseReservedStock(productId, storeId, quantity)
                .map(webInventoryMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/{productId}/{storeId}/available")
    public Mono<ResponseEntity<Integer>> getAvailableStock(
            @PathVariable String productId,
            @PathVariable String storeId) {

        return inventoryService.getAvailableStock(productId, storeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(0));
    }

    @GetMapping("/{productId}/{storeId}/can-fulfill")
    public Mono<ResponseEntity<Boolean>> canFulfillOrder(
            @PathVariable String productId,
            @PathVariable String storeId,
            @RequestParam @Valid @Positive Integer quantity) {

        return inventoryService.getAvailableStock(productId, storeId)
                .map(available -> available >= quantity && available > 0)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInventory(@PathVariable String id) {
        // TODO: Implementar cuando el service tenga método delete
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build());
    }
}