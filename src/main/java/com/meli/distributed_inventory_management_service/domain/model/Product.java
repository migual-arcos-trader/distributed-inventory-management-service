package com.meli.distributed_inventory_management_service.domain.model;

import lombok.Builder;
import lombok.Value;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;

@Value
@Builder
public class Product {

    private static final int MIN_PRICE = 0;

    String id;
    String sku;
    String name;
    String description;
    BigDecimal price;
    String category;

    public Mono<Boolean> isValid() {
        return Mono.fromCallable(() -> Objects.nonNull(sku) && !sku.isBlank() &&
                Objects.nonNull(name) && !name.isBlank() &&
                Objects.nonNull(price) && price.compareTo(BigDecimal.ZERO) > MIN_PRICE
        );
    }

}
