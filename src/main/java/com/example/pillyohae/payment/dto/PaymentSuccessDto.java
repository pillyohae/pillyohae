package com.example.pillyohae.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class PaymentSuccessDto {
    private final UUID orderId;
    private final String amount;
    private final String paymentKey;

    public PaymentSuccessDto(UUID orderId, String amount, String paymentKey) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
    }
}
