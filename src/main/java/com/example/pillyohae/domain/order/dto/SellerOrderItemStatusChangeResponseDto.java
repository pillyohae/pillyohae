package com.example.pillyohae.domain.order.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SellerOrderItemStatusChangeResponseDto {
    private Long orderItemId;
    private String status;

    public SellerOrderItemStatusChangeResponseDto(Long orderItemId, String status) {
        this.orderItemId = orderItemId;
        this.status = status;
    }
}
