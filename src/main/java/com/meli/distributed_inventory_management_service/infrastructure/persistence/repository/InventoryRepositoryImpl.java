package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.domain.repository.InventoryRepository;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final ReactiveInventoryJpaRepository jpaRepository;
    private final DatabaseClient databaseClient;
    private final InventoryMapper inventoryMapper;

    @Override
    public Mono<InventoryItem> findById(String id) {
        return jpaRepository.findById(id)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public Mono<InventoryItem> findByProductAndStore(String productId, String storeId) {
        return jpaRepository.findByProductIdAndStoreId(productId, storeId)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public Flux<InventoryItem> findByStore(String storeId) {
        return jpaRepository.findByStoreId(storeId)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public Flux<InventoryItem> findByProduct(String productId) {
        return jpaRepository.findByProductId(productId)
                .map(inventoryMapper::toDomain);
    }

    @Override
    @Transactional
    public Mono<InventoryItem> save(InventoryItem item) {
        InventoryEntity entity = inventoryMapper.toEntity(item);
        return jpaRepository.save(entity)
                .map(inventoryMapper::toDomain);
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
            InventoryEntity entity = inventoryMapper.toEntity(item);

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
                    .map(inventoryMapper::toDomain);
        });
    }

    @Override
    public Mono<Boolean> existsByProductAndStore(String productId, String storeId) {
        return jpaRepository.existsByProductIdAndStoreId(productId, storeId);
    }

}
