package com.example.main.order.repository;


import com.example.common.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderQueryRepository {

    @Query("select o from Order o join fetch o.orderProducts op where o.id = :orderId")
    Optional<Order> findByOrderIdWithOrderProducts(UUID orderId);
}
