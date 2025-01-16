package com.example.pillyohae.order.dto;

import com.example.pillyohae.global.entity.address.ShippingAddress;
import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.example.pillyohae.order.entity.status.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderDetailResponseDto {

    private final OrderInfoDto orderInfo;
    private final List<OrderProductDto> orderProducts;

    public OrderDetailResponseDto(OrderInfoDto orderInfo, List<OrderProductDto> orderProducts) {
        this.orderInfo = orderInfo;
        this.orderProducts = orderProducts;
    }

    @NoArgsConstructor(force = true)
    @Getter
    public static class OrderInfoDto{
        private final UUID orderId;
        private final OrderStatus orderStatus;
        private final String orderName;
        private final LocalDateTime paidAt;
        private final String imageUrl;
        private final ShippingAddress shippingAddress;

        @QueryProjection
        public OrderInfoDto(UUID orderId, OrderStatus orderStatus, String orderName, LocalDateTime paidAt, String imageUrl, ShippingAddress shippingAddress) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;
            this.orderName = orderName;
            this.paidAt = paidAt;
            this.imageUrl = imageUrl;
            this.shippingAddress = shippingAddress;
        }
    }

    @NoArgsConstructor(force = true)
    @Getter
    public static class OrderProductDto {
        private final Long orderItemId;
        private final String orderItemName;
        private final Integer orderItemQuantity;
        private final Long orderItemPrice;
        private final OrderProductStatus orderProductStatus;
        @QueryProjection
        public OrderProductDto(Long orderItemId, String orderItemName, Integer orderItemQuantity, Long orderItemPrice, OrderProductStatus orderProductStatus) {
            this.orderItemId = orderItemId;
            this.orderItemName = orderItemName;
            this.orderItemQuantity = orderItemQuantity;
            this.orderItemPrice = orderItemPrice;
            this.orderProductStatus = orderProductStatus;
        }
    }
}