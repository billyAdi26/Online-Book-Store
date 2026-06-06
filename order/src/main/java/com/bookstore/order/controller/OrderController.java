package com.bookstore.order.controller;

import com.bookstore.order.controller.response.ApiResponse;
import com.bookstore.order.dto.CartItem;
import com.bookstore.order.dto.OrderDTO;
import com.bookstore.order.repository.OrderSummary;
import com.bookstore.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/customer/{customerId}/summary")
    public ResponseEntity<ApiResponse<OrderSummary>> getOrderSummary(
            @PathVariable String customerId) {
        OrderSummary data = orderService.getOrderSummary(customerId);
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }


    @GetMapping("/cart/{customerId}")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCart(
            @PathVariable String customerId) {
        List<CartItem> data = orderService.getCart(customerId);
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

    @PostMapping("/cart/{customerId}/items")
    public ResponseEntity<ApiResponse<Void>> addToCart(
            @PathVariable String customerId,
            @RequestBody CartItem item) {
        orderService.addToCart(customerId, item);
        return ResponseEntity.ok(new ApiResponse<>(200, null, "Item added to cart"));
    }

    @DeleteMapping("/cart/{customerId}/items/{bookId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable String customerId,
            @PathVariable UUID bookId) {
        orderService.removeFromCart(customerId, bookId);
        return ResponseEntity.ok(new ApiResponse<>(200, null, "Item removed from cart"));
    }


    @PostMapping("/checkout/{customerId}")
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(
            @PathVariable String customerId) {
        OrderDTO data = orderService.placeOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, data, "Order placed successfully"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @PathVariable UUID orderId) {
        OrderDTO data = orderService.getOrderById(orderId);
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByCustomer(
            @PathVariable String customerId) {
        List<OrderDTO> data = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

}
