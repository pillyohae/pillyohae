package com.example.pillyohae.order.dto;

import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(force = true)
public class BuyerOrderDetailInfo {
    private final List<BuyerOrderProductInfo> orderItemInfos;

    public BuyerOrderDetailInfo(List<BuyerOrderProductInfo> orderItemInfos) {
        this.orderItemInfos = orderItemInfos;
    }

    @NoArgsConstructor
    @Getter
    public static class BuyerOrderProductInfo{
        private Long orderItemId;
        private String orderItemName;
        private Integer orderItemQuantity;
        private Long orderItemPrice;
        private OrderItemStatus orderItemStatus;
        @QueryProjection
        public BuyerOrderProductInfo(Long orderItemId, String orderItemName, Integer orderItemQuantity, Long orderItemPrice, OrderItemStatus orderItemStatus) {
            this.orderItemId = orderItemId;
            this.orderItemName = orderItemName;
            this.orderItemQuantity = orderItemQuantity;
            this.orderItemPrice = orderItemPrice;
            this.orderItemStatus = orderItemStatus;
        }
    }
}
