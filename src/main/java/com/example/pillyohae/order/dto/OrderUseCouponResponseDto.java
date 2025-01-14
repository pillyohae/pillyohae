package com.example.pillyohae.order.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderUseCouponResponseDto {
    UUID orderId;
    Long couponId;
    Long discountedPrice;

    public OrderUseCouponResponseDto(UUID orderId, Long couponId, Long discountedPrice) {
        this.orderId = orderId;
        this.couponId = couponId;
        this.discountedPrice = discountedPrice;
    }
}
