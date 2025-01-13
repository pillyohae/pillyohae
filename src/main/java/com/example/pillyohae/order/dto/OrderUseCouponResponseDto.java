package com.example.pillyohae.order.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderUseCouponResponseDto {
    UUID orderId;
    Long couponId;
    Double discountedPrice;

    public OrderUseCouponResponseDto(UUID orderId, Long couponId, Double discountedPrice) {
        this.orderId = orderId;
        this.couponId = couponId;
        this.discountedPrice = discountedPrice;
    }
}
