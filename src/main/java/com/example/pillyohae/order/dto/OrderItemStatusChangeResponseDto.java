package com.example.pillyohae.order.dto;

import lombok.Getter;

@Getter
public class OrderItemStatusChangeResponseDto {
    private Long orderProductId;
    private String status;

    public OrderItemStatusChangeResponseDto(Long orderProductId, String status) {
        this.orderProductId = orderProductId;
        this.status = status;
    }
}
