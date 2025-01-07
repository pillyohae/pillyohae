package com.example.pillyohae.domain.order.dto;

import com.example.pillyohae.domain.order.entity.status.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class BuyerOrderInfo {
    private UUID orderId;
    private OrderStatus orderStatus;
    private String orderName;
    private LocalDateTime orderTime;

    @QueryProjection
    public BuyerOrderInfo(UUID orderId, OrderStatus orderStatus, String orderName, LocalDateTime orderTime) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.orderTime = orderTime;
    }
}
