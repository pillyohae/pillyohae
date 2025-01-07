package com.example.pillyohae.domain.order.entity.status;

public enum OrderItemStatus {
    PENDING("주문 접수 중"),
    CANCELLED("주문 취소"),
    READY_FOR_SHIPMENT("배송 준비 완료"),
    SHIPPED("배송 중"),
    DELIVERED("배송 완료"),
    DELIVERY_FAILED("배송 실패"),
    RETURN_REQUESTED("반품 요청됨"),
    RETURNED("반품 완료");

    private final String description;

    OrderItemStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}