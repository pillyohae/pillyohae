package com.example.pillyohae.domain.order.repository;

import com.example.pillyohae.domain.order.entity.Order;
import com.example.pillyohae.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Long order(Order order);
}
