package com.example.pillyohae.cart.dto;

import lombok.Getter;

@Getter
public class CartProductDetailResponseDto {

    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Long price;
    private final Integer quantity;

    public CartProductDetailResponseDto(Long productId, String productName, String imageUrl, Long price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }
}
