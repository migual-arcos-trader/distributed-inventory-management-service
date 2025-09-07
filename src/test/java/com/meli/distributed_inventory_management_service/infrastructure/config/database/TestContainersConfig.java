package com.meli.distributed_inventory_management_service.infrastructure.config.database;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    @Primary
    public GenericContainer<?> h2Container() {
        // Para tests que necesiten un container real, aunque usemos H2 en memoria
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("oscarfonts/h2"))
                .withExposedPorts(1521)
                .withEnv("H2_OPTIONS", "-ifNotExists");

        container.start();
        return container;
    }
}