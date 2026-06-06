package com.bookstore.order.mapper;

import com.bookstore.order.dto.OrderItemsDTO;
import com.bookstore.order.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemsMapper {
    @Mapping(source = "order.id", target = "orderId")
    OrderItemsDTO toDto(OrderItems entity);
    List<OrderItemsDTO> toDtoList(List<OrderItems> entities);
}
