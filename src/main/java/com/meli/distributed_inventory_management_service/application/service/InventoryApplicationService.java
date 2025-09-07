package com.meli.distributed_inventory_management_service.application.service;

import com.meli.distributed_inventory_management_service.application.usecase.*;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import com.meli.distributed_inventory_management_service.domain.service.InventoryDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.meli.distributed_inventory_management_service.application.constants.ApplicationConstants.*;

@Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private final InventoryDomainService domainInventoryDomainService;
    private final GetAllInventoryUseCase getAllInventoryUseCase;
    private final GetInventoryByIdUseCase getInventoryByIdUseCase;
    private final GetInventoryByProductAndStoreUseCase getInventoryByProductAndStoreUseCase;
    private final GetInventoryByStoreUseCase getInventoryByStoreUseCase;
    private final GetInventoryByProductUseCase getInventoryByProductUseCase;
    private final CreateInventoryUseCase createInventoryUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseReservationUseCase releaseReservationUseCase;
    private final DeleteInventoryUseCase deleteInventoryUseCase;
    private final CheckInventoryExistsUseCase checkInventoryExistsUseCase;

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Flux<InventoryItem> getAllInventory() {
        return getAllInventoryUseCase.execute();
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<InventoryItem> getInventoryById(String id) {
        return getInventoryByIdUseCase.execute(id);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Flux<InventoryItem> getInventoryByStore(String storeId) {
        return getInventoryByStoreUseCase.execute(storeId);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Flux<InventoryItem> getInventoryByProduct(String productId) {
        return getInventoryByProductUseCase.execute(productId);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<InventoryItem> createInventory(String productId, String storeId, Integer initialStock) {
        return createInventoryUseCase.execute(productId, storeId, initialStock);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<InventoryItem> updateStock(String productId, String storeId, Integer quantity, UpdateType updateType) {
        return updateStockUseCase.execute(productId, storeId, quantity, updateType.toString());
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<InventoryItem> reserveStock(String productId, String storeId, Integer quantity) {
        return reserveStockUseCase.execute(productId, storeId, quantity);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<InventoryItem> releaseReservedStock(String productId, String storeId, Integer quantity) {
        return releaseReservationUseCase.execute(productId, storeId, quantity);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<Boolean> deleteInventory(String id) {
        return deleteInventoryUseCase.execute(id);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<InventoryItem> updateStockWithRetry(String productId, String storeId, Integer quantity, UpdateType updateType) {
        return domainInventoryDomainService.updateStockWithRetry(productId, storeId, quantity, updateType);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER, maxDelay = RETRY_MAX_DELAY)
    )
    public Mono<Integer> getAvailableStock(String productId, String storeId) {
        return domainInventoryDomainService.getAvailableStock(productId, storeId);
    }

}