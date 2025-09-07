package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.application.dto.inventory.InventoryRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.InventoryResponseDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.StockUpdateRequestDTO;
import com.meli.distributed_inventory_management_service.application.service.InventoryApplicationService;
import com.meli.distributed_inventory_management_service.infrastructure.web.mapper.WebInventoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "APIs para gestión de inventario distribuido")
public class InventoryController {

    private final InventoryApplicationService inventoryApplicationService;
    private final WebInventoryMapper webInventoryMapper;

    @GetMapping
    @Operation(summary = "Obtener todo el inventario", description = "Retorna todos los items de inventario del sistema")
    @ApiResponse(responseCode = "200", description = "Inventario obtenido exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = InventoryResponseDTO.class)))
    public Flux<InventoryResponseDTO> getAllInventory() {
        return inventoryApplicationService.getAllInventory()
                .map(webInventoryMapper::toResponseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inventario por ID", description = "Retorna un item de inventario específico por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item de inventario encontrado"),
            @ApiResponse(responseCode = "404", description = "Item de inventario no encontrado")
    })
    public Mono<ResponseEntity<InventoryResponseDTO>> getInventoryById(
            @Parameter(description = "ID del item de inventario", example = "item-123")
            @PathVariable String id) {

        return inventoryApplicationService.getInventoryById(id)
                .map(webInventoryMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Obtener inventario por tienda", description = "Retorna todos los items de inventario de una tienda específica")
    @ApiResponse(responseCode = "200", description = "Inventario de la tienda obtenido exitosamente")
    public Flux<InventoryResponseDTO> getInventoryByStore(
            @Parameter(description = "ID de la tienda", example = "store-1")
            @PathVariable String storeId) {

        return inventoryApplicationService.getInventoryByStore(storeId)
                .map(webInventoryMapper::toResponseDTO);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Obtener inventario por producto", description = "Retorna todos los items de inventario de un producto específico")
    @ApiResponse(responseCode = "200", description = "Inventario del producto obtenido exitosamente")
    public Flux<InventoryResponseDTO> getInventoryByProduct(
            @Parameter(description = "ID del producto", example = "prod-1")
            @PathVariable String productId) {

        return inventoryApplicationService.getInventoryByProduct(productId)
                .map(webInventoryMapper::toResponseDTO);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo item de inventario", description = "Crea un nuevo registro de inventario para un producto en una tienda")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item de inventario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El item de inventario ya existe")
    })
    public Mono<ResponseEntity<InventoryResponseDTO>> createInventory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para crear el item de inventario",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InventoryRequestDTO.class))
            )
            @Valid @RequestBody InventoryRequestDTO request) {

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
    @Operation(summary = "Actualizar stock", description = "Actualiza el stock de un producto en una tienda específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Item de inventario no encontrado")
    })
    public Mono<ResponseEntity<InventoryResponseDTO>> updateStockByProductAndStore(
            @Parameter(description = "ID del producto", example = "prod-1")
            @RequestParam String productId,

            @Parameter(description = "ID de la tienda", example = "store-1")
            @RequestParam String storeId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para actualizar el stock",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StockUpdateRequestDTO.class))
            )
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
    @Operation(summary = "Reservar stock", description = "Reserva una cantidad específica de stock de un producto en una tienda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock reservado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Item de inventario no encontrado")
    })
    public Mono<ResponseEntity<InventoryResponseDTO>> reserveStock(
            @Parameter(description = "ID del producto", example = "prod-1")
            @PathVariable String productId,

            @Parameter(description = "ID de la tienda", example = "store-1")
            @PathVariable String storeId,

            @Parameter(description = "Cantidad a reservar", example = "10")
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
    @Operation(summary = "Liberar stock reservado", description = "Libera stock previamente reservado de un producto en una tienda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock liberado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Item de inventario no encontrado")
    })
    public Mono<ResponseEntity<InventoryResponseDTO>> releaseReservedStock(
            @Parameter(description = "ID del producto", example = "prod-1")
            @PathVariable String productId,

            @Parameter(description = "ID de la tienda", example = "store-1")
            @PathVariable String storeId,

            @Parameter(description = "Cantidad a liberar", example = "5")
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
    @Operation(summary = "Obtener stock disponible", description = "Obtiene la cantidad de stock disponible para un producto en una tienda")
    @ApiResponse(responseCode = "200", description = "Stock disponible obtenido exitosamente")
    public Mono<ResponseEntity<Integer>> getAvailableStock(
            @Parameter(description = "ID del producto", example = "prod-1")
            @PathVariable String productId,

            @Parameter(description = "ID de la tienda", example = "store-1")
            @PathVariable String storeId) {

        return inventoryApplicationService.getAvailableStock(productId, storeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(0));
    }

    @GetMapping("/{productId}/{storeId}/can-fulfill")
    @Operation(summary = "Verificar si puede cumplir orden", description = "Verifica si hay suficiente stock disponible para cumplir una orden")
    @ApiResponse(responseCode = "200", description = "Verificación completada exitosamente")
    public Mono<ResponseEntity<Boolean>> canFulfillOrder(
            @Parameter(description = "ID del producto", example = "prod-1")
            @PathVariable String productId,

            @Parameter(description = "ID de la tienda", example = "store-1")
            @PathVariable String storeId,

            @Parameter(description = "Cantidad requerida", example = "50")
            @RequestParam @Valid @Positive Integer quantity) {

        return inventoryApplicationService.getAvailableStock(productId, storeId)
                .map(available -> available >= quantity && available > 0)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(false));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar item de inventario", description = "Elimina un item de inventario específico")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item de inventario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Item de inventario no encontrado")
    })
    public Mono<ResponseEntity<Void>> deleteInventory(
            @Parameter(description = "ID del item de inventario", example = "item-123")
            @PathVariable String id) {

        return inventoryApplicationService.deleteInventory(id)
                .map(deleted -> deleted ?
                        ResponseEntity.noContent().build() :
                        ResponseEntity.notFound().build());
    }
}