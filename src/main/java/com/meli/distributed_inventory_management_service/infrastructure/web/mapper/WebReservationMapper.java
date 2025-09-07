package com.meli.distributed_inventory_management_service.infrastructure.web.mapper;

import com.meli.distributed_inventory_management_service.application.dto.inventory.ReservationResponseDTO;
import com.meli.distributed_inventory_management_service.domain.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebReservationMapper {

    @Mapping(source = "reservationId", target = "reservationId")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "storeId", target = "storeId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "reservedAt")
    @Mapping(source = "expiresAt", target = "expiresAt")
    @Mapping(source = "correlationId", target = "correlationId")
    ReservationResponseDTO toResponseDTO(Reservation reservation);
}