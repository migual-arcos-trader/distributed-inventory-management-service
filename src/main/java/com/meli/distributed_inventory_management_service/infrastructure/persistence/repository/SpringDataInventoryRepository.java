package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.domain.exception.ConcurrentUpdateException;
import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.repository.InventoryRepository;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper.PersistenceInventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SpringDataInventoryRepository implements InventoryRepository {

    private static final int errorNumberOfRowsUpdated = 0;
    private final ReactiveInventoryJpaRepository jpaRepository;
    private final DatabaseClient databaseClient;
    private final PersistenceInventoryMapper persistenceInventoryMapper;

    @Override
    public Flux<InventoryItem> findAll() {
        return jpaRepository.findAll()
                .map(persistenceInventoryMapper::toDomain);
    }

    @Override
    public Mono<InventoryItem> findById(String id) {
        return jpaRepository.findById(id)
                .map(persistenceInventoryMapper::toDomain);
    }

    @Override
    public Mono<InventoryItem> findByProductAndStore(String productId, String storeId) {
        return jpaRepository.findByProductIdAndStoreId(productId, storeId)
                .map(persistenceInventoryMapper::toDomain);
    }

    @Override
    public Flux<InventoryItem> findByStore(String storeId) {
        return jpaRepository.findByStoreId(storeId)
                .map(persistenceInventoryMapper::toDomain);
    }

    @Override
    public Flux<InventoryItem> findByProduct(String productId) {
        return jpaRepository.findByProductId(productId)
                .map(persistenceInventoryMapper::toDomain);
    }

    @Override
    @Transactional
    public Mono<InventoryItem> save(InventoryItem item) {
        InventoryEntity entity = persistenceInventoryMapper.toEntity(item);
        return jpaRepository.save(entity)
                .map(persistenceInventoryMapper::toDomain);
    }

    @Override
    @Transactional
    public Mono<Boolean> delete(String id) {
        return jpaRepository.deleteById(id)
                .then(Mono.just(true))
                .onErrorReturn(false);
    }

    @Override
    @Transactional
    public Mono<InventoryItem> updateWithVersionCheck(InventoryItem item, Long expectedVersion) {
        return Mono.defer(() -> {
            InventoryEntity entity = persistenceInventoryMapper.toEntity(item);

            return jpaRepository.findById(item.getId())
                    .flatMap(existingEntity -> {
                        if (!existingEntity.getVersion().equals(expectedVersion)) {
                            return Mono.error(new OptimisticLockingFailureException(
                                    "Version mismatch. Expected: " + expectedVersion +
                                            ", Actual: " + existingEntity.getVersion()
                            ));
                        }
                        return jpaRepository.save(entity);
                    })
                    .map(persistenceInventoryMapper::toDomain);
        });
    }

    @Override
    public Mono<Boolean> existsByProductAndStore(String productId, String storeId) {
        return jpaRepository.existsByProductIdAndStoreId(productId, storeId);
    }

    @Transactional
    public Mono<InventoryItem> updateWithVersionCheckNative(InventoryItem item, Long expectedVersion) {
        String updateSql = """
                 UPDATE inventory_items\s
                 SET current_stock = :currentStock,\s
                     reserved_stock = :reservedStock,
                     minimum_stock_level = :minimumStockLevel,
                     maximum_stock_level = :maximumStockLevel,
                     last_updated = :lastUpdated,
                     version = version + 1
                 WHERE id = :id AND version = :expectedVersion
                \s""";

        return databaseClient.sql(updateSql)
                .bind("currentStock", item.getCurrentStock())
                .bind("reservedStock", item.getReservedStock())
                .bind("minimumStockLevel", item.getMinimumStockLevel())
                .bind("maximumStockLevel", item.getMaximumStockLevel())
                .bind("lastUpdated", item.getLastUpdated())
                .bind("id", item.getId())
                .bind("expectedVersion", expectedVersion)
                .fetch()
                .rowsUpdated()
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated == errorNumberOfRowsUpdated) {
                        return Mono.error(new OptimisticLockingFailureException(
                                "Concurrent update detected for item: " + item.getId() +
                                        ". Expected version: " + expectedVersion
                        ));
                    }
                    return jpaRepository.findById(item.getId());
                })
                .map(persistenceInventoryMapper::toDomain);
    }

    public Mono<Integer> updateStockWithVersion(String id, Integer newStock, Long version) {
        return jpaRepository.updateStockWithVersion(id, newStock, version);
    }

    public Flux<InventoryItem> findLowStockItems(String storeId, Integer threshold) {
        return jpaRepository.findLowStockItems(storeId, threshold)
                .map(persistenceInventoryMapper::toDomain);
    }

    public Mono<Integer> reserveStockWithVersion(String id, Integer quantity, Long version) {
        return jpaRepository.reserveStockWithVersion(id, quantity, version);
    }

    public Mono<Integer> releaseReservedStockWithVersion(String id, Integer quantity, Long version) {
        return jpaRepository.releaseReservedStockWithVersion(id, quantity, version);
    }

    public Mono<Integer> countOverstockItems(String storeId) {
        return jpaRepository.countOverstockItems(storeId);
    }

    @Transactional
    public Mono<InventoryItem> atomicUpdate(InventoryItem item, Long expectedVersion) {
        return updateWithVersionCheckNative(item, expectedVersion)
                .onErrorResume(OptimisticLockingFailureException.class, ex ->
                        Mono.error(new ConcurrentUpdateException(
                                item.getProductId(),
                                item.getStoreId(),
                                expectedVersion,
                                item.getVersion()
                        ))
                );
    }

}
