package com.example.pillyohae.order.dto;

import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@NoArgsConstructor(force = true)
public class OrderSellerInfoDto {
    private final UUID orderId;
    private final Long orderProductId;
    private final OrderProductStatus orderStatus;
    private final String orderName;
    private final LocalDateTime paidAt;
    private final String imageUrl;
    private final Long orderPrice;
    private final Integer quantity;

    @QueryProjection
    public OrderSellerInfoDto(UUID orderId, Long orderProductId, OrderProductStatus orderStatus, String orderName, LocalDateTime paidAt, String imageUrl, Long orderPrice, Integer quantity) {
        this.orderId = orderId;
        this.orderProductId = orderProductId;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.paidAt = paidAt;
        this.imageUrl = imageUrl;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }
}




