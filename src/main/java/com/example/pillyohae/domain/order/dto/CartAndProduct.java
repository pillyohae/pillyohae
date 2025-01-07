package com.example.pillyohae.domain.order.dto;

import com.example.pillyohae.cart.entity.Cart;
import com.example.pillyohae.product.entity.Product;
import lombok.Getter;

@Getter
public class CartAndProduct {
    private final Cart cart;
    private final Product product;

    public CartAndProduct(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
    }
}
