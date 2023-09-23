package hu.pantasandor.inventoryservice.mapper;

import hu.pantasandor.inventoryservice.dto.InventoryResponse;
import hu.pantasandor.inventoryservice.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InventoryMapper {
    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    @Mapping(target = "isInStock", expression = "java(isInStock(inventory))")
    InventoryResponse doMap(Inventory inventory);

    default boolean isInStock(Inventory inventory) {
        return inventory.getQuantity() > 0;
    }
}

