package com.meli.distributed_inventory_management_service.infrastructure.web.mapper;

import com.meli.distributed_inventory_management_service.application.dto.inventory.EventResponseDTO;
import com.meli.distributed_inventory_management_service.domain.model.InventoryUpdateEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebEventMapper {

    @Mapping(source = "eventId", target = "eventId")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "storeId", target = "storeId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "updateType", target = "updateType")
    @Mapping(source = "source", target = "source")
    @Mapping(source = "correlationId", target = "correlationId")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "errorDetails", target = "errorDetails")
    EventResponseDTO toResponseDTO(InventoryUpdateEvent event);
}