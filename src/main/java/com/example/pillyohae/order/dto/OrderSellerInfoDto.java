package com.example.pillyohae.order.dto;

import com.example.pillyohae.global.entity.address.ShippingAddress;
import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)
public class OrderSellerInfoDto {
    private final Long orderProductId;
    private final ShippingAddress shippingAddress;
    private final OrderProductStatus status;
    private final String productName;
    private final LocalDateTime paidAt;
    private final String imageUrl;
    private final Long productPrice;
    private final Integer quantity;

    @QueryProjection
    public OrderSellerInfoDto(Long orderProductId, OrderProductStatus productStatus, String productName, LocalDateTime paidAt, ShippingAddress shippingAddress, String imageUrl, Long productPrice, Integer quantity) {
        this.shippingAddress = shippingAddress;
        this.orderProductId = orderProductId;
        this.status = productStatus;
        this.productName = productName;
        this.paidAt = paidAt;
        this.imageUrl = imageUrl;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }
}

