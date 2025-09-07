package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.inventory.InventoryRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.InventoryResponseDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.StockUpdateRequestDTO;
import com.meli.distributed_inventory_management_service.application.service.InventoryApplicationService;
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

    private final InventoryApplicationService inventoryApplicationService;
    private final WebInventoryMapper webInventoryMapper;

    @GetMapping
    public Flux<InventoryResponseDTO> getAllInventory() {
        return inventoryApplicationService.getAllInventory()
                .map(webInventoryMapper::toResponseDTO);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<InventoryResponseDTO>> getInventoryById(@PathVariable String id) {
        return inventoryApplicationService.getInventoryById(id)
                .map(webInventoryMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/store/{storeId}")
    public Flux<InventoryResponseDTO> getInventoryByStore(@PathVariable String storeId) {
        return inventoryApplicationService.getInventoryByStore(storeId)
                .map(webInventoryMapper::toResponseDTO);
    }

    @GetMapping("/product/{productId}")
    public Flux<InventoryResponseDTO> getInventoryByProduct(@PathVariable String productId) {
        return inventoryApplicationService.getInventoryByProduct(productId)
                .map(webInventoryMapper::toResponseDTO);
    }

    @PostMapping
    public Mono<ResponseEntity<InventoryResponseDTO>> createInventory(@Valid @RequestBody InventoryRequestDTO request) {
        return inventoryApplicationService.createInventory(
                        request.productId(),
                        request.storeId(),
                        request.currentStock()
                )
                .map(webInventoryMapper::toResponseDTO)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(IllegalArgumentException.class,
                        error -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @PutMapping("/stock")
    public Mono<ResponseEntity<InventoryResponseDTO>> updateStockByProductAndStore(
            @RequestParam String productId,
            @RequestParam String storeId,
            @Valid @RequestBody StockUpdateRequestDTO request) {

        return inventoryApplicationService.updateStockWithRetry(
                        productId,
                        storeId,
                        request.quantity(),
                        request.updateType()
                )
                .map(webInventoryMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class,
                        error -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @PostMapping("/{productId}/{storeId}/reserve")
    public Mono<ResponseEntity<InventoryResponseDTO>> reserveStock(
            @PathVariable String productId,
            @PathVariable String storeId,
            @RequestParam @Valid @Positive Integer quantity) {

        return inventoryApplicationService.reserveStock(productId, storeId, quantity)
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

        return inventoryApplicationService.releaseReservedStock(productId, storeId, quantity)
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

        return inventoryApplicationService.getAvailableStock(productId, storeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(0));
    }

    @GetMapping("/{productId}/{storeId}/can-fulfill")
    public Mono<ResponseEntity<Boolean>> canFulfillOrder(
            @PathVariable String productId,
            @PathVariable String storeId,
            @RequestParam @Valid @Positive Integer quantity) {

        return inventoryApplicationService.getAvailableStock(productId, storeId)
                .map(available -> available >= quantity && available > 0)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInventory(@PathVariable String id) {
        return inventoryApplicationService.deleteInventory(id)
                .map(deleted -> deleted ?
                        ResponseEntity.noContent().build() :
                        ResponseEntity.notFound().build());
    }

}
