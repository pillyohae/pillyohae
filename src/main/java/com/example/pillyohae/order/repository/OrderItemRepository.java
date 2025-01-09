package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Long order(Order order);
}
