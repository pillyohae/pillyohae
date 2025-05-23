package com.example.main.payment.repository;

import com.example.common.payment.entity.PaymentFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 결제 실패 내역을 관리하는 리포지토리
 */
public interface PaymentFailLogRepository extends JpaRepository<PaymentFailLog, Long> {

}
