package com.meli.distributed_inventory_management_service.infrastructure.config.database;

import com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper.InventoryMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestMapperConfig {

    @Bean
    public InventoryMapper inventoryMapper() {
        return Mappers.getMapper(InventoryMapper.class);
    }

}
