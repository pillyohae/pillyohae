package com.example.pillyohae.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

/**
 * 결제 실패 저장용 로그
 */
@Entity
@Getter
public class PaymentFailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String version;

    private String traceId;

    private String errorCode;

    private String message;

    public PaymentFailLog(String version, String traceId, String errorCode, String message) {
        this.version = version;
        this.traceId = traceId;
        this.errorCode = errorCode;
        this.message = message;
    }

    protected PaymentFailLog() {

    }
}
