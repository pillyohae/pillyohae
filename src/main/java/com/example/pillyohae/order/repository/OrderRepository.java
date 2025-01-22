package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderQueryRepository {

    @Query("select o from Order o join fetch OrderProduct op where o.id = :orderId")
    Optional<Order> findByOrderIdWithOrderProducts(UUID orderId);
}
