package com.meli.distributed_inventory_management_service.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory_items")
public class InventoryEntity {

    @Id
    private String id;
    private String productId;
    private String storeId;
    private Integer currentStock;
    private Integer reservedStock;
    private Integer minimumStockLevel;
    private Integer maximumStockLevel;
    private LocalDateTime lastUpdated;

    @Version
    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
