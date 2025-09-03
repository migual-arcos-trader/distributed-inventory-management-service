package com.meli.distributed_inventory_management_service.domain.service;

import com.meli.distributed_inventory_management_service.domain.exception.ConcurrentUpdateException;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import com.meli.distributed_inventory_management_service.domain.repository.InventoryRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InventoryService {

    public static final int DELAY = 100;
    public static final int MAX_ATTEMPTS = 5;
    public static final int MAX_AVAILABLE_STOCK = 1000;
    public static final int MAX_DELAY = 1000;
    public static final int MIN_AVAILABLE_STOCK = 0;
    public static final int MULTIPLIER = 2;
    public static final long VERSION_DEFAULT = 0L;

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = DELAY, multiplier = MULTIPLIER, maxDelay = MAX_DELAY)
    )
    public Mono<InventoryItem> updateStockWithRetry(String productId, String storeId,
                                                    Integer quantity, UpdateType updateType) {
        return inventoryRepository.findByProductAndStore(productId, storeId)
                .switchIfEmpty(createNewInventoryItem(productId, storeId))
                .flatMap(existingItem -> existingItem.updateStock(quantity, updateType)
                        .flatMap(updatedItem -> inventoryRepository.updateWithVersionCheck(updatedItem, existingItem.getVersion())
                                .onErrorMap(OptimisticLockingFailureException.class, ex ->
                                        new ConcurrentUpdateException(
                                                productId, storeId,
                                                existingItem.getVersion(),
                                                updatedItem.getVersion()
                                        )
                                )));
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = DELAY, multiplier = MULTIPLIER)
    )
    public Mono<InventoryItem> reserveStock(String productId, String storeId, Integer quantity) {
        return inventoryRepository.findByProductAndStore(productId, storeId)
                .flatMap(item -> item.reserveStock(quantity))
                .flatMap(inventoryRepository::save)
                .onErrorMap(OptimisticLockingFailureException.class, ex ->
                        new ConcurrentUpdateException(productId, storeId, null, null)
                );
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = DELAY)
    )
    public Mono<InventoryItem> releaseReservedStock(String productId, String storeId, Integer quantity) {
        return inventoryRepository.findByProductAndStore(productId, storeId)
                .flatMap(item -> item.releaseReservedStock(quantity))
                .flatMap(inventoryRepository::save)
                .onErrorMap(OptimisticLockingFailureException.class, ex ->
                        new ConcurrentUpdateException(productId, storeId, null, null)
                );
    }

    public Mono<Integer> getAvailableStock(String productId, String storeId) {
        return inventoryRepository.findByProductAndStore(productId, storeId)
                .flatMap(InventoryItem::getAvailableStock)
                .defaultIfEmpty(MIN_AVAILABLE_STOCK);
    }

    private Mono<InventoryItem> createNewInventoryItem(String productId, String storeId) {
        InventoryItem newItem = InventoryItem.builder()
                .productId(productId)
                .storeId(storeId)
                .currentStock(MIN_AVAILABLE_STOCK)
                .reservedStock(MIN_AVAILABLE_STOCK)
                .minimumStockLevel(MIN_AVAILABLE_STOCK)
                .maximumStockLevel(MAX_AVAILABLE_STOCK)
                .lastUpdated(java.time.LocalDateTime.now())
                .version(VERSION_DEFAULT)
                .build();

        return inventoryRepository.save(newItem);
    }

    public Mono<InventoryItem> updateStockWithoutRetry(String productId, String storeId,
                                                       Integer quantity, UpdateType updateType) {
        return updateStockWithRetry(productId, storeId, quantity, updateType);
    }

}
