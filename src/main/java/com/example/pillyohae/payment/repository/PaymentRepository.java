package com.example.pillyohae.payment.repository;

import com.example.pillyohae.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
