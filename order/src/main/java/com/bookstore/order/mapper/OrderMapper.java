package com.bookstore.order.mapper;

import com.bookstore.order.dto.OrderDTO;
import com.bookstore.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDto(Order entity);
    List<OrderDTO> toDtoList(List<Order> entities);
}
