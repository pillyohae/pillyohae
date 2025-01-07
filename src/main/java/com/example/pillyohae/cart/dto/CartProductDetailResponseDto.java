package com.example.pillyohae.cart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartProductDetailResponseDto {

    private final Long productId;
    private final String name;
    private final String ImageUrl;
    private final Long price;
    private final Integer quantity;
}
