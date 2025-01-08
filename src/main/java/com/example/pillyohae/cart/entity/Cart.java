package com.example.pillyohae.cart.entity;

import com.example.pillyohae.global.entity.BaseCreatedTimeEntity;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Cart extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer quantity;

    public Cart() {
    }

    public Cart(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public void updateQuantity(Integer quantity) {
        if (quantity < 1) {
            this.quantity = 1;
        }
        this.quantity = quantity;
    }
}
