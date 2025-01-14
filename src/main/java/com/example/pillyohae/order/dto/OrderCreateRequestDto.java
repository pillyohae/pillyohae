package com.example.pillyohae.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequestDto {
    @NotNull
    private List<ProductOrderInfo> productInfos;
    private List<Long> couponIds;

    public OrderCreateRequestDto(List<ProductOrderInfo> productInfos, List<Long> couponIds) {
        this.productInfos = productInfos;
        this.couponIds = couponIds;
    }

    @Getter
    @NoArgsConstructor
    public static class ProductOrderInfo {
        @NotNull
        @Positive
        private Long productId;
        @NotNull
        @Positive @Max(20)
        private Integer quantity;

        public ProductOrderInfo(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}