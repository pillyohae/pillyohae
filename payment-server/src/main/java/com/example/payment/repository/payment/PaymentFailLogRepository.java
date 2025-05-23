package com.example.payment.repository.payment;


import com.example.common.payment.entity.PaymentFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentFailLogRepository extends JpaRepository<PaymentFailLog, Long> {
}
