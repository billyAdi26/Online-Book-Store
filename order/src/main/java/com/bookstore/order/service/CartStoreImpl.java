package com.bookstore.order.service;

import com.bookstore.order.dto.CartItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartStoreImpl implements CartStore{

    private final Map<String, List<CartItem>> carts = new ConcurrentHashMap<>();

    @Override
    public List<CartItem> getCart(String customerId) {
        return carts.getOrDefault(customerId, new ArrayList<>());
    }

    @Override
    public void addItem(String customerId, CartItem item) {
        carts.computeIfAbsent(customerId, k -> new ArrayList<>());
        List<CartItem> items = carts.get(customerId);

        items.stream()
                .filter(i -> i.getBookId().equals(item.getBookId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + item.getQuantity()),
                        () -> items.add(item)
                );
    }

    @Override
    public void removeItem(String customerId, UUID bookId) {
        List<CartItem> items = carts.get(customerId);
        if (items != null) items.removeIf(i -> i.getBookId().equals(bookId));
    }

    @Override
    public void clearCart(String customerId) {
        carts.remove(customerId);
    }

    @Override
    public BigDecimal getCartTotal(String customerId) {
        return carts.getOrDefault(customerId, new ArrayList<>())
                .stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
