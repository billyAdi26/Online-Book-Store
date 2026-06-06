package com.bookstore.order.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderSummary {
    String getCustomerId();
    long getTotalOrders();
    BigDecimal getTotalSpent();
    LocalDateTime getLastOrderDate();
}
