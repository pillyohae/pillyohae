package com.example.pillyohae.order.dto;

import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.example.pillyohae.order.entity.status.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderPageSellerResponseDto {

    private final List<OrderInfoDto> OrderInfoDtos;
    private final PageInfo pageInfo;

    public OrderPageSellerResponseDto(List<OrderInfoDto> OrderInfoDtos, PageInfo pageInfo) {
        this.OrderInfoDtos = OrderInfoDtos;
        this.pageInfo = pageInfo;
    }

    @Getter
    @NoArgsConstructor(force = true)
    public static class OrderInfoDto {
        private final UUID orderId;
        private final Long orderProductId;
        private final OrderProductStatus orderStatus;
        private final String orderName;
        private final LocalDateTime paidAt;
        private final String imageUrl;
        private final Long orderPrice;
        private final Integer quantity;

        @QueryProjection
        public OrderInfoDto(UUID orderId, Long orderProductId, OrderProductStatus orderStatus, String orderName, LocalDateTime paidAt, String imageUrl, Long orderPrice, Integer quantity) {
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


    @Getter
    @NoArgsConstructor
    public static class PageInfo {
        private Long pageNumber;
        private Long pageSize;

        public PageInfo(Long pageNumber, Long pageSize) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
        }
    }
}
