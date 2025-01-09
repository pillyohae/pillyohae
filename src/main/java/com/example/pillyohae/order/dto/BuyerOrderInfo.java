package com.example.pillyohae.order.dto;

import com.example.pillyohae.order.entity.status.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class BuyerOrderInfo {
    private final UUID orderId;
    private final OrderStatus orderStatus;
    private final String orderName;
    private final LocalDateTime orderTime;

    @QueryProjection
    public BuyerOrderInfo(UUID orderId, OrderStatus orderStatus, String orderName, LocalDateTime orderTime) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.orderTime = orderTime;
    }
}
