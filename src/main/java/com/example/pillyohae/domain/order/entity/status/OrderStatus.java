package com.example.pillyohae.domain.order.entity.status;

import lombok.Getter;

public enum OrderStatus {
    // 초기 상태
    PENDING("주문 접수 중"),
    PAYMENT_CONFIRMED("결제 확인됨"),
    CANCELLED("주문 취소됨"),

    // 처리 및 준비 상태
    PROCESSING("처리 중"),
    STOCK_CHECK("재고 확인 중"),
    READY_FOR_SHIPMENT("배송 준비 완료"),

    // 배송 및 배달 상태
    SHIPPED("배송 중"),
    DELIVERED("배송 완료"),
    DELIVERY_FAILED("배송 실패"),

    // 특수 상태
    PARTIALLY_SHIPPED("부분 배송"),
    RETURN_REQUESTED("반품 요청됨"),
    RETURN_PROCESSING("반품 처리 중"),
    REFUNDED("환불 완료");

    private final String description; // 한글 설명 값

    // 생성자
    OrderStatus(String description) {
        this.description = description;
    }

    // Getter
    public String getDescription() {
        return description;
    }
    }