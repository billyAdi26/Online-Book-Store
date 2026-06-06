package com.bookstore.order.service;

import com.bookstore.order.dto.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CartStore {
    List<CartItem> getCart(String customerId);

    void addItem(String customerId, CartItem item);

    void removeItem(String customerId, UUID bookId);

    void clearCart(String customerId);

    BigDecimal getCartTotal(String customerId);

}
