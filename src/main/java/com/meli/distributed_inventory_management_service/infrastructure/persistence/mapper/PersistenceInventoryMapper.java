package com.meli.distributed_inventory_management_service.infrastructure.persistence.mapper;

import com.meli.distributed_inventory_management_service.domain.model.InventoryItem;
import com.meli.distributed_inventory_management_service.infrastructure.persistence.entity.InventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Objects;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface PersistenceInventoryMapper {

    PersistenceInventoryMapper INSTANCE = Mappers.getMapper(PersistenceInventoryMapper.class);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    InventoryEntity toEntity(InventoryItem domain);

    InventoryItem toDomain(InventoryEntity entity);

    default InventoryItem toDomainWithVersion(InventoryEntity entity, Long expectedVersion) {
        Objects.requireNonNull(entity, "InventoryEntity cannot be null");
        Objects.requireNonNull(expectedVersion, "Expected version cannot be null");

        if (!entity.getVersion().equals(expectedVersion)) {
            throw new OptimisticLockingFailureException(
                    "Version mismatch. Expected: " + expectedVersion +
                            ", Actual: " + entity.getVersion()
            );
        }
        return toDomain(entity);
    }

}