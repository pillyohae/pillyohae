package com.example.pillyohae.cart.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartUpdateResponseDto {

    private final Long cartId;

    private final Integer quantity;
}
