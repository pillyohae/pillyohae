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

    private final List<OrderInfo> orderInfos;
    private final PageInfo pageInfo;

    public OrderPageResponseDto(List<OrderInfo> orderInfos, PageInfo pageInfo) {
        this.orderInfos = orderInfos;
        this.pageInfo = pageInfo;
    }

    @Getter
    @NoArgsConstructor(force = true)
    public class OrderInfo {
        private final UUID orderId;
        private final OrderStatus orderStatus;
        private final String orderName;
        private final LocalDateTime paidAt;
        private final String imageUrl;

        @QueryProjection
        public OrderInfo(UUID orderId, OrderStatus orderStatus, String orderName, LocalDateTime paidAt, String imageUrl) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;
            this.orderName = orderName;
            this.paidAt = paidAt;
            this.imageUrl = imageUrl;
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
