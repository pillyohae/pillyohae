package com.example.main.order.repository;

import com.example.common.order.entity.Order;
import com.example.common.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    Long order(Order order);
}
