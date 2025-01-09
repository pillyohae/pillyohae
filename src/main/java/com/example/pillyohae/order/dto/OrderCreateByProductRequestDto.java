package com.example.pillyohae.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class OrderCreateByProductRequestDto {
    @NotNull
    @Positive
    private final Long productId;
    @NotNull
    @Positive @Max(20)
    private final Long quantity;

    public OrderCreateByProductRequestDto(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
