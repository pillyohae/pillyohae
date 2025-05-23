package com.example.common.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class PersonaMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productMessageId;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_productId")
    private Product product;

    public PersonaMessage(String message) {
        this.message = message;
    }

    public void assignToProduct(Product product) {
        this.product = product;
        if (!product.getPersonaMessages().contains(this)) {
            product.getPersonaMessages().add(this);
        }
    }

    public PersonaMessage() {

    }
}
