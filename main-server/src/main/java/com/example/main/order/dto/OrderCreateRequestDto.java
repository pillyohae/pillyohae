package com.example.main.order.dto;

import com.example.main.order.service.OrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Creates an order by delegating to the external processing service.
 *
 * @see OrderService#createOrderByProducts(String, OrderCreateRequestDto)
 */


@Getter
@NoArgsConstructor
public class OrderCreateRequestDto {
    @NotNull
    private List<ProductOrderInfo> productInfos;
    private List<UUID> couponIds;

    public OrderCreateRequestDto(List<ProductOrderInfo> productInfos, List<UUID> couponIds) {
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