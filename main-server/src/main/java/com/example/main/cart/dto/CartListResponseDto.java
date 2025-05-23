package com.example.main.cart.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartListResponseDto {

    private final Long userId;

    private final Long totalPrice;

    private final List<CartProductDetailResponseDto> products;
}
