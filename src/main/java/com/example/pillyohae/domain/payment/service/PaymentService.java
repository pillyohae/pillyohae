package com.example.pillyohae.domain.payment.service;

import com.example.pillyohae.domain.payment.dto.PaymentDataDto;
import com.example.pillyohae.domain.payment.entity.Payment;
import com.example.pillyohae.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void savePayment(PaymentDataDto paymentDataDto) {
        Payment payment = Payment.builder()
                .fromDto(paymentDataDto)
                .build();
        System.out.println(payment.getId());
        paymentRepository.save(payment);

    }
}