package com.example.main.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartUpdateRequestDto {

    @NotNull(message = "상품 수량을 확인해 주세요")
    private final Integer quantity;
}
