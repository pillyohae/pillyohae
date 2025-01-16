package com.example.pillyohae.order.dto;

import com.example.pillyohae.order.entity.status.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderPageResponseDto {

    private final List<OrderInfoDto> orderInfoDtos;
    private final PageInfo pageInfo;

    public OrderPageResponseDto(List<OrderInfoDto> orderInfoDtos, PageInfo pageInfo) {
        this.orderInfoDtos = orderInfoDtos;
        this.pageInfo = pageInfo;
    }

    @Getter
    @NoArgsConstructor(force = true)
    public static class OrderInfoDto {
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
