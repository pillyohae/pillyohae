package com.example.pillyohae.payment.service;

import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.payment.dto.PaymentDataDto;
import com.example.pillyohae.payment.entity.Payment;
import com.example.pillyohae.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;


    @Transactional
    public void savePayment(PaymentDataDto paymentDataDto) {
        Payment payment = Payment.builder()
                .fromDto(paymentDataDto)
                .build();
        paymentRepository.save(payment);
    }

}
