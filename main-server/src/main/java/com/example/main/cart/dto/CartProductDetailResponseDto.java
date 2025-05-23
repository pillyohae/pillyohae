package com.example.main.cart.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(force = true)
public class CartProductDetailResponseDto {

    private final Long cartId;
    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Long price;
    private final Integer quantity;

    @QueryProjection
    public CartProductDetailResponseDto(Long cartId, Long productId, String productName,
        String imageUrl, Long price, Integer quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    // 테스트 전용
    @Builder
    public CartProductDetailResponseDto(Long price, Integer quantity) {
        this.cartId = 0L;
        this.productId = 0L;
        this.productName = "test";
        this.imageUrl = "test";
        this.price = price;
        this.quantity = quantity;
    }
}
