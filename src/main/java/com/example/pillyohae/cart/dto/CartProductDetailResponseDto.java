package com.example.pillyohae.cart.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CartProductDetailResponseDto {

    private final Long cartId;
    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Long price;
    private final Integer quantity;

    @Builder
    public CartProductDetailResponseDto(Long cartId, Long productId, String productName,
        String imageUrl, Long price, Integer quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }
}
