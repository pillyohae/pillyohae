package com.example.pillyohae.order.dto;

import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.querydsl.core.annotations.QueryProjection;
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
        private Integer orderItemQuantity;
        private Double orderItemPrice;
        private OrderItemStatus orderItemStatus;
        @QueryProjection
        public BuyerOrderItemInfo(Long orderItemId, String orderItemName, Integer orderItemQuantity, Double orderItemPrice, OrderItemStatus orderItemStatus) {
            this.orderItemId = orderItemId;
            this.orderItemName = orderItemName;
            this.orderItemQuantity = orderItemQuantity;
            this.orderItemPrice = orderItemPrice;
            this.orderItemStatus = orderItemStatus;
        }
    }
}
