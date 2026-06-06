package com.bookstore.order.repository;

import com.bookstore.order.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemsRepository extends JpaRepository<OrderItems, UUID> {
    List<OrderItems> findByOrder_Id(UUID orderId);
}