package com.meli.distributed_inventory_management_service.infrastructure.web.mapper;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.infrastructure.web.dto.InventoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebInventoryMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "storeId", target = "storeId")
    @Mapping(source = "currentStock", target = "currentStock")
    @Mapping(source = "reservedStock", target = "reservedStock")
    @Mapping(source = "minimumStockLevel", target = "minimumStockLevel")
    @Mapping(source = "maximumStockLevel", target = "maximumStockLevel")
    @Mapping(source = "lastUpdated", target = "lastUpdated")
    @Mapping(source = "version", target = "version")
    InventoryResponseDTO toResponseDTO(InventoryItem inventoryItem);
}