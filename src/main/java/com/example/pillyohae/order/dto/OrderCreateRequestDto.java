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
    private List<ProductOrderInfo> productInfos;
    private List<Long> couponIds;

    @Getter
    @NoArgsConstructor
    public static class ProductOrderInfo {
        @NotNull
        @Positive
        private Long productId;
        @NotNull
        @Positive @Max(20)
        private Integer quantity;
    }
}