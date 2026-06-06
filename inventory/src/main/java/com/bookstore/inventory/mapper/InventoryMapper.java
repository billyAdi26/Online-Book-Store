package com.bookstore.inventory.mapper;

import com.bookstore.inventory.dto.InventoryDTO;
import com.bookstore.inventory.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toEntity(InventoryDTO dto);

    InventoryDTO toDto(Inventory entity);
}
