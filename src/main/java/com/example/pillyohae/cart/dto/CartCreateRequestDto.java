package com.example.pillyohae.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartCreateRequestDto {

    @NotNull(message = "상품을 선택해 주세요.")
    private final Long productId;

    @NotNull(message = "상품 수량을 선택해 주세요.")
    private final Integer quantity;

}
