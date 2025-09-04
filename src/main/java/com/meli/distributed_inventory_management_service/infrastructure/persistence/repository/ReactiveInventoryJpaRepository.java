package com.meli.distributed_inventory_management_service.infrastructure.persistence.repository;

import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveInventoryJpaRepository extends ReactiveCrudRepository<InventoryEntity, String> {

    Mono<InventoryEntity> findByProductIdAndStoreId(String productId, String storeId);

    Flux<InventoryEntity> findByStoreId(String storeId);

    Flux<InventoryEntity> findByProductId(String productId);

    Mono<Boolean> existsByProductIdAndStoreId(String productId, String storeId);

    @Modifying
    @Query("UPDATE inventory_items SET current_stock = :newStock, version = version + 1 WHERE id = :id AND version = :version")
    Mono<Integer> updateStockWithVersion(String id, Integer newStock, Long version);

    @Query("SELECT * FROM inventory_items WHERE store_id = :storeId AND (current_stock - reserved_stock) < :threshold")
    Flux<InventoryEntity> findLowStockItems(String storeId, Integer threshold);

    @Modifying
    @Query("UPDATE inventory_items SET reserved_stock = reserved_stock + :quantity, version = version + 1 WHERE id = :id AND version = :version")
    Mono<Integer> reserveStockWithVersion(String id, Integer quantity, Long version);

    @Modifying
    @Query("UPDATE inventory_items SET reserved_stock = reserved_stock - :quantity, version = version + 1 WHERE id = :id AND version = :version")
    Mono<Integer> releaseReservedStockWithVersion(String id, Integer quantity, Long version);

    @Query("SELECT COUNT(*) FROM inventory_items WHERE store_id = :storeId AND current_stock > maximum_stock_level")
    Mono<Integer> countOverstockItems(String storeId);

}
