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
@NoArgsConstructor(force = true)
public class OrderDetailSellerResponseDto {
    private final OrderDetailSellerResponseDto.OrderInfoDto orderInfo;
    private final OrderDetailSellerResponseDto.OrderProductDto orderProduct;

    public OrderDetailSellerResponseDto(OrderDetailSellerResponseDto.OrderInfoDto orderInfo, OrderProductDto orderProduct) {
        this.orderInfo = orderInfo;
        this.orderProduct = orderProduct;

    }

    @NoArgsConstructor(force = true)
    @Getter
    public static class OrderInfoDto{
        private final UUID orderId;
        private final OrderStatus orderStatus;
        private final LocalDateTime paidAt;
        private final ShippingAddress shippingAddress;
        @QueryProjection
        public OrderInfoDto(UUID orderId, OrderStatus orderStatus, LocalDateTime paidAt, ShippingAddress shippingAddress) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;

            this.paidAt = paidAt;

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
