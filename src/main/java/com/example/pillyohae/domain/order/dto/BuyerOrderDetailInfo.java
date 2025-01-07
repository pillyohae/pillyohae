package com.example.pillyohae.domain.order.dto;

import com.example.pillyohae.domain.order.entity.status.OrderItemStatus;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(force = true)
public class BuyerOrderDetailInfo {
    private final List<BuyerOrderItemInfo> orderItemInfos;

    public BuyerOrderDetailInfo(List<BuyerOrderItemInfo> orderItemInfos) {
        this.orderItemInfos = orderItemInfos;
    }

    @NoArgsConstructor
    @Getter
    public static class BuyerOrderItemInfo{
        private Long orderItemId;
        private String orderItemName;
        private Long orderItemQuantity;
        private Double orderItemPrice;
        private OrderItemStatus orderItemStatus;
        @QueryProjection
        public BuyerOrderItemInfo(Long orderItemId, String orderItemName, Long orderItemQuantity, Double orderItemPrice, OrderItemStatus orderItemStatus) {
            this.orderItemId = orderItemId;
            this.orderItemName = orderItemName;
            this.orderItemQuantity = orderItemQuantity;
            this.orderItemPrice = orderItemPrice;
            this.orderItemStatus = orderItemStatus;
        }
    }
}
