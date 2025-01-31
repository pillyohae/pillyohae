package com.example.pillyohae.payment.repository;

import com.example.pillyohae.payment.entity.PaymentFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentFailLogRepostiroy extends JpaRepository<PaymentFailLog, Long> {
}
