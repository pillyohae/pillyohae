package com.example.common.payment.entity;

import com.example.common.payment.entity.toss_variable.PayMethod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 결제 정보를 저장하는 엔티티 클래스
 * <p>
 * - 결제 성공 시 데이터 저장
 * - 주문과 연결된 결제 정보 포함
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mid; // 가맹점 ID

    @Column(nullable = false)
    private String version; // 결제 API 버전

    @Column(nullable = false)
    private String paymentKey; // 결제 키

    @Column
    private String status; // 결제 상태 (승인, 실패 등)

    @Column(nullable = false)
    private UUID orderId; // 주문 ID (주문과 연결됨)

    @Column
    private String orderName; // 주문명

    @Column
    private String requestedAt; // 결제 요청 시간

    @Column
    private String approvedAt; // 결제 승인 시간

    @Column
    private Integer totalAmount; // 총 결제 금액

    @Column
    private Integer balanceAmount; // 남은 결제 금액

    @Enumerated(EnumType.STRING)
    @Column
    private PayMethod method; // 결제 방식 (카드, 가상계좌 등)

    @Builder
    public Payment(String mid, String version, String paymentKey, String status, UUID orderId,
        String orderName, String requestedAt, String approvedAt, Integer totalAmount,
        Integer balanceAmount, PayMethod method) {
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
