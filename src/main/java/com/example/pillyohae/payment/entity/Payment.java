package com.example.pillyohae.payment.entity;

import com.example.pillyohae.payment.dto.PaymentDataDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String mid;
    @Column(nullable = false)
    private String version;
    @Column(nullable = false)
    private String paymentKey;
    @Column
    private String status;
    @Column(nullable = false)
    private String orderId;
    @Column
    private String orderName;
    @Column
    private String requestedAt;
    @Column
    private String approvedAt;
    @Column
    private Long totalAmount;
    @Column
    private Long balanceAmount;
    @Enumerated(EnumType.STRING)
    @Column
    private PayMethod method;

    // 연관관계 설정은 하지 않습니다

    @Builder
    public Payment(String mid, String version, String paymentKey, String status, String orderId, String orderName, String requestedAt, String approvedAt, Long totalAmount, Long balanceAmount, PayMethod method) {

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

    public static class PaymentBuilder {
        public PaymentBuilder fromDto(PaymentDataDto dto) {
            this.mid = dto.getMid();
            this.version = dto.getVersion();
            this.paymentKey = dto.getPaymentKey();
            this.status = dto.getStatus();
            this.orderId = dto.getOrderId();
            this.orderName = dto.getOrderName();
            this.requestedAt = dto.getRequestedAt();
            this.approvedAt = dto.getApprovedAt();
            this.totalAmount = dto.getTotalAmount();
            this.balanceAmount = dto.getBalanceAmount();
            this.method = dto.getMethod();
            return this;
        }
    }
}
