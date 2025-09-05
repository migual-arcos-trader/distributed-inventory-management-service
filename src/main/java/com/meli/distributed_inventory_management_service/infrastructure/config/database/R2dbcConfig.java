package com.meli.distributed_inventory_management_service.infrastructure.config.database;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {

    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.create(connectionFactory);
    }

}
