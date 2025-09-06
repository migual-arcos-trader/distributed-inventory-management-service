package com.meli.distributed_inventory_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class DistributedInventoryManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedInventoryManagementServiceApplication.class, args);
    }

}
