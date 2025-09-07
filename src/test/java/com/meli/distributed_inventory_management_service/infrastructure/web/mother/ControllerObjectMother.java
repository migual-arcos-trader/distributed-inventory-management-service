package com.meli.distributed_inventory_management_service.infrastructure.web.mother;

import com.meli.distributed_inventory_management_service.application.dto.inventory.InventoryRequestDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.InventoryResponseDTO;
import com.meli.distributed_inventory_management_service.application.dto.inventory.StockUpdateRequestDTO;
import com.meli.distributed_inventory_management_service.domain.model.UpdateType;
import com.meli.distributed_inventory_management_service.infrastructure.web.constants.ControllerTestConstants;

import java.time.LocalDateTime;

public class ControllerObjectMother {

    private ControllerObjectMother() {}

    public static InventoryRequestDTO.InventoryRequestDTOBuilder basicInventoryRequest() {
        return InventoryRequestDTO.builder()
                .productId(ControllerTestConstants.TEST_PRODUCT_ID)
                .storeId(ControllerTestConstants.TEST_STORE_ID)
                .currentStock(ControllerTestConstants.INITIAL_STOCK)
                .reservedStock(ControllerTestConstants.RESERVED_STOCK)
                .minimumStockLevel(5)
                .maximumStockLevel(200);
    }

    public static InventoryRequestDTO createInventoryRequest() {
        return basicInventoryRequest().build();
    }

    public static InventoryResponseDTO.InventoryResponseDTOBuilder basicInventoryResponse() {
        return InventoryResponseDTO.builder()
                .id(ControllerTestConstants.TEST_ITEM_ID)
                .productId(ControllerTestConstants.TEST_PRODUCT_ID)
                .storeId(ControllerTestConstants.TEST_STORE_ID)
                .currentStock(ControllerTestConstants.INITIAL_STOCK)
                .reservedStock(ControllerTestConstants.RESERVED_STOCK)
                .minimumStockLevel(5)
                .maximumStockLevel(200)
                .lastUpdated(LocalDateTime.now())
                .version(1L);
    }

    public static InventoryResponseDTO createInventoryResponse() {
        return basicInventoryResponse().build();
    }

    public static StockUpdateRequestDTO.StockUpdateRequestDTOBuilder basicStockUpdateRequest() {
        return StockUpdateRequestDTO.builder()
                .quantity(ControllerTestConstants.QUANTITY_TO_UPDATE)
                .updateType(UpdateType.PURCHASE);
    }

    public static StockUpdateRequestDTO createStockUpdateRequest() {
        return basicStockUpdateRequest().build();
    }

    public static StockUpdateRequestDTO createStockUpdateRequest(UpdateType updateType, Integer quantity) {
        return basicStockUpdateRequest()
                .updateType(updateType)
                .quantity(quantity)
                .build();
    }
}