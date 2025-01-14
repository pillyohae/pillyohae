package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderProduct, Long> {
    Long order(Order order);
}
