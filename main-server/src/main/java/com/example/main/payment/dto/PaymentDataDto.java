package com.example.main.payment.dto;

import com.example.common.payment.entity.toss_variable.PayMethod;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentDataDto {
    private String mid;
    private String version;
    private String paymentKey;
    private String status;
    private String orderId;
    private String orderName;
    private String requestedAt;
    private String approvedAt;
    private Long totalAmount;
    private Long balanceAmount;
    private PayMethod method;
    @Builder
    public PaymentDataDto(String mid, String version, String paymentKey, String status, String orderId, String orderName, String requestedAt, String approvedAt, Long totalAmount, Long balanceAmount, PayMethod method) {
        this.mid = mid;
        this.version = version;
        this.paymentKey = paymentKey;
        this.status = status;
        this.orderId = orderId;
        this.orderName = orderName;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
        this.balanceAmount = balanceAmount;
        this.method = method;
    }
}
