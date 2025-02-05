package com.example.pillyohae.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

/**
 * 결제 실패 내역을 저장하는 엔티티
 * <p>
 * - 결제 실패 시 로그를 남기기 위한 클래스
 */
@Entity
@Getter
public class PaymentFailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String version; // 결제 API 버전
    private String traceId; // 실패 추적 ID
    private String errorCode; // 실패 코드
    private String message; // 실패 메시지

    /**
     * 결제 실패 로그 생성자
     */
    public PaymentFailLog(String version, String traceId, String errorCode, String message) {
        this.version = version;
        this.traceId = traceId;
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * JPA를 위한 기본 생성자 (PROTECTED)
     */
    protected PaymentFailLog() {
    }
}
