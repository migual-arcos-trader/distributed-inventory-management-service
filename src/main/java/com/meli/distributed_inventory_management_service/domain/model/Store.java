package com.meli.distributed_inventory_management_service.domain.model;

import lombok.Builder;
import lombok.Value;
import reactor.core.publisher.Mono;

@Value
@Builder
public class Store {

    String id;
    String name;
    String location;
    String address;
    String timezone;
    boolean isActive;

    public Mono<Boolean> canReceiveUpdates() {
        return Mono.just(isActive);
    }

}
