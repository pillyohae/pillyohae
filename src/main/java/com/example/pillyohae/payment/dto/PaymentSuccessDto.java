package com.example.pillyohae.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class PaymentSuccessDto {
    private final UUID orderId;
    private final Long amount;
    private final String paymentKey;

    public PaymentSuccessDto(UUID orderId, Long amount, String paymentKey) {
        if (orderId == null) {
            throw new IllegalArgumentException("주문 ID는 필수입니다.");
        }
        if (amount == null ) {
            throw new IllegalArgumentException("결제 금액은 필수입니다.");
        }
        if (paymentKey == null || paymentKey.isEmpty()) {
            throw new IllegalArgumentException("결제 키는 필수입니다.");
        }

        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
    }
}
