package com.meli.distributed_inventory_management_service.infrastructure.config.database;

import com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper.PersistenceInventoryMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestPersistenceMapperConfig {

    @Bean
    public PersistenceInventoryMapper persistenceInventoryMapper() {
        return Mappers.getMapper(PersistenceInventoryMapper.class);
    }

}
