package com.example.pillyohae.cart.dto;

import lombok.Getter;

@Getter
public class CartProductDetailResponseDto {

    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Long price;
    private final Integer quantity;

    /**
     * Constructs a new CartProductDetailResponseDto with the specified product details.
     *
     * @param productId The unique identifier of the product
     * @param productName The name of the product
     * @param imageUrl The URL of the product's image
     * @param price The price of the product
     * @param quantity The quantity of the product in the cart
     */
    public CartProductDetailResponseDto(Long productId, String productName, String imageUrl, Long price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }
}
