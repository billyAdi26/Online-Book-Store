package com.bookstore.order.repository;

import com.bookstore.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  List<Order> findByCustomerId(String customerId);

  @Query(value = """
            SELECT
                o.customer_id        AS customerId,
                COUNT(o.id)          AS totalOrders,
                SUM(o.total_amount)  AS totalSpent,
                MAX(o.order_date)    AS lastOrderDate
            FROM orders o
            WHERE o.customer_id = :customerId
            GROUP BY o.customer_id
            """, nativeQuery = true)
  OrderSummary getOrderSummaryByCustomerId(@Param("customerId")String customerId);
}
