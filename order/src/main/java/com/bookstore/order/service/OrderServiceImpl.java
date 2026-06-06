package com.bookstore.order.service;

import com.bookstore.order.client.InventoryClient;
import com.bookstore.order.controller.response.StockCheckResponse;
import com.bookstore.order.dto.CartItem;
import com.bookstore.order.dto.OrderDTO;
import com.bookstore.order.entity.Order;
import com.bookstore.order.entity.OrderItems;
import com.bookstore.order.mapper.OrderMapper;
import com.bookstore.order.repository.OrderRepository;
import com.bookstore.order.repository.OrderSummary;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartStore cartStore;
    private final InventoryClient inventoryClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            CartStore cartStore,
                            InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.cartStore = cartStore;
        this.inventoryClient = inventoryClient;
    }


    @Override
    public List<CartItem> getCart(String customerId) {
        return cartStore.getCart(customerId);
    }

    @Override
    public void addToCart(String customerId, CartItem item) {
        cartStore.addItem(customerId, item);
    }

    @Override
    public void removeFromCart(String customerId, UUID bookId) {
        cartStore.removeItem(customerId, bookId);
    }


    @Override
    public OrderDTO getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with id %s not found", orderId)));
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(String customerId) {
        return orderMapper.toDtoList(orderRepository.findByCustomerId(customerId));
    }

    @Override
    public OrderSummary getOrderSummary(String customerId) {
        return orderRepository.getOrderSummaryByCustomerId(customerId);
    }


    @Override
    @Transactional
    public OrderDTO placeOrder(String customerId) {
        List<CartItem> cartItems = cartStore.getCart(customerId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for customer: " + customerId);
        }

        boolean hasInvalidItems = cartItems.stream()
                .anyMatch(item -> item.getQuantity() <= 0 || item.getPrice() == null);

        if (hasInvalidItems) {
            throw new RuntimeException("Cart contains invalid items for customer: " + customerId);
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(customerId);
        order.setStatus("PENDING");
        order.setSagaStatus("STARTED");
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(calculateTotal(cartItems));

        Order savedOrder = orderRepository.save(order);

        List<OrderItems> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItems item = new OrderItems();
                    item.setOrder(savedOrder);
                    item.setBookId(cartItem.getBookId());
                    item.setQuantity(cartItem.getQuantity());
                    item.setPriceAtPurchase(cartItem.getPrice());
                    return item;
                })
                .toList();

        savedOrder.setItems(orderItems);

        try {
            boolean allAvailable = cartItems.stream()
                    .allMatch(cartItem -> {
                        StockCheckResponse check = inventoryClient.checkStock(
                                cartItem.getBookId(), cartItem.getQuantity());
                        return check.available();
                    });

            if (!allAvailable) {
                throw compensate(savedOrder, "Insufficient stock for one or more items", new ArrayList<>());
            }

            savedOrder.setSagaStatus("STOCK_CHECKED");
            orderRepository.save(savedOrder);

        } catch (Exception e) {
            throw compensate(savedOrder, "Stock check failed: " + e.getMessage(), new ArrayList<>());
        }

        List<CartItem> reserved = new ArrayList<>();
        try {
            for (CartItem cartItem : cartItems) {
                inventoryClient.reserveStock(cartItem.getBookId(), cartItem.getQuantity());
                reserved.add(cartItem);
            }
            savedOrder.setSagaStatus("STOCK_RESERVED");
            orderRepository.save(savedOrder);

        } catch (Exception e) {
            throw compensate(savedOrder, "Stock reservation failed: " + e.getMessage(), reserved);
        }

        savedOrder.setStatus("CONFIRMED");
        savedOrder.setSagaStatus("COMPLETED");
        orderRepository.save(savedOrder);

        cartStore.clearCart(customerId);

        return orderMapper.toDto(savedOrder);
    }


    private RuntimeException compensate(Order order, String reason, List<CartItem> reservedItems) {
        reservedItems
                .forEach(item -> {
                    try {
                        inventoryClient.releaseStock(item.getBookId(), item.getQuantity());
                    } catch (Exception e) {
                        // best effort release — if inventory service is down, we still cancel the order
                    }
                });

        order.setStatus("CANCELLED");
        order.setSagaStatus("FAILED");
        orderRepository.save(order);

        return new RuntimeException("Order failed — " + reason);
    }


    private String generateOrderNumber() {
        return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private BigDecimal calculateTotal(List<CartItem> items) {
        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
