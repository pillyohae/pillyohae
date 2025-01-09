package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderQueryRepository {

}
