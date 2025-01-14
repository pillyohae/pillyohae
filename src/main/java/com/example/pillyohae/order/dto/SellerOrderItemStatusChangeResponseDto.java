package com.example.pillyohae.order.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SellerOrderItemStatusChangeResponseDto {
    private Long orderProductId;
    private String status;

    public SellerOrderItemStatusChangeResponseDto(Long orderProductId, String status) {
        this.orderProductId = orderProductId;
        this.status = status;
    }
}
