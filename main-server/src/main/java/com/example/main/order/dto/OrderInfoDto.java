package com.example.main.order.dto;

import com.example.common.order.entity.status.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@NoArgsConstructor(force = true)
public class OrderInfoDto {
    private final UUID orderId;
    private final OrderStatus orderStatus;
    private final String orderName;
    private final LocalDateTime paidAt;
    private final String imageUrl;
    private final Long orderPrice;

    @QueryProjection
    public OrderInfoDto(UUID orderId, OrderStatus orderStatus, String orderName, LocalDateTime paidAt, String imageUrl, Long orderPrice) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.paidAt = paidAt;
        this.imageUrl = imageUrl;
        this.orderPrice = orderPrice;
    }
}


