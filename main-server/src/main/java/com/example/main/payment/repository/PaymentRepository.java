package com.example.main.payment.repository;

import com.example.common.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 결제 정보를 관리하는 리포지토리
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
