package com.meli.distributed_inventory_management_service.infrastructure.web.controller;

import com.meli.distributed_inventory_management_service.infrastructure.config.database.TestContainersConfig;
import com.meli.distributed_inventory_management_service.infrastructure.config.security.TestSecurityConfig;
import com.meli.distributed_inventory_management_service.infrastructure.web.constants.ControllerTestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.meli.distributed_inventory_management_service.infrastructure.web.constants.ControllerTestConstants.*;

@SpringBootTest
@AutoConfigureWebTestClient
@Import({TestContainersConfig.class, TestSecurityConfig.class})
@ActiveProfiles("testcontainers")
@DisplayName("InventoryController Testcontainers Integration Tests")
class InventoryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Should get all inventory from real database")
    void shouldGetAllInventoryFromRealDatabase() {
        // Act & Assert
        webTestClient.get()
                .uri(INVENTORY_BASE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(4); // 4 items en data-test.sql
    }

    @Test
    @DisplayName("Should get inventory by ID from real database")
    void shouldGetInventoryByIdFromRealDatabase() {
        // Act & Assert
        webTestClient.get()
                .uri(INVENTORY_BY_ID_PATH, TEST_ITEM_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(TEST_ITEM_ID)
                .jsonPath("$.productId").isEqualTo(TEST_PRODUCT_ID)
                .jsonPath("$.storeId").isEqualTo(TEST_STORE_ID)
                .jsonPath("$.currentStock").isEqualTo(INITIAL_STOCK)
                .jsonPath("$.reservedStock").isEqualTo(RESERVED_STOCK);
    }

    @Test
    @DisplayName("Should get inventory by store from real database")
    void shouldGetInventoryByStoreFromRealDatabase() {
        // Act & Assert
        webTestClient.get()
                .uri(INVENTORY_BY_STORE_PATH, TEST_STORE_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(2); // 2 items para test-store-1 en data-test.sql
    }

    @Test
    @DisplayName("Should get available stock from real database")
    void shouldGetAvailableStockFromRealDatabase() {
        // Act & Assert
        webTestClient.get()
                .uri(AVAILABLE_STOCK_PATH, TEST_PRODUCT_ID, TEST_STORE_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(INITIAL_STOCK - RESERVED_STOCK); // 100 - 10 = 90
    }
}