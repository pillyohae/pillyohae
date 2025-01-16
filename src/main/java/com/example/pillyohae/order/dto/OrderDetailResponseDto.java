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
@NoArgsConstructor
public class OrderDetailResponseDto {

    private OrderInfoDto orderInfo;
    private List<OrderProductDto> orderProducts;

    public OrderDetailResponseDto(OrderInfoDto orderInfo, List<OrderProductDto> orderProducts) {
        this.orderInfo = orderInfo;
        this.orderProducts = orderProducts;
    }

    @NoArgsConstructor
    @Getter
    public static class OrderInfoDto{
        private UUID orderId;
        private OrderStatus orderStatus;
        private String orderName;
        private LocalDateTime paidAt;
        private String imageUrl;
        private ShippingAddress shippingAddress;

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

    @NoArgsConstructor
    @Getter
    public static class OrderProductDto {
        private Long orderItemId;
        private String orderItemName;
        private Integer orderItemQuantity;
        private Long orderItemPrice;
        private OrderProductStatus orderProductStatus;
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
