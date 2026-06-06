package com.bookstore.order.service;

import com.bookstore.order.dto.CartItem;
import com.bookstore.order.dto.OrderDTO;
import com.bookstore.order.repository.OrderSummary;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<CartItem> getCart(String customerId);
    void addToCart(String customerId, CartItem item);
    void removeFromCart(String customerId, UUID bookId);

    OrderDTO placeOrder(String customerId);
    OrderDTO getOrderById(UUID orderId);
    List<OrderDTO> getOrdersByCustomerId(String customerId);
    OrderSummary getOrderSummary(String customerId);
}
