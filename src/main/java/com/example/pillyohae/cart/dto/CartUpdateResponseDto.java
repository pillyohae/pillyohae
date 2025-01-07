package com.example.pillyohae.cart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartUpdateResponseDto {

    private final Long productId;

    private final Integer quantity;
}
