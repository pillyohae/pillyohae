package com.example.common.product.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "nutrients")
public class Nutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nutrientId;

    @Column(name = "name", unique = true)
    private String name;

    private String description;

    public Nutrient(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Nutrient() {
    }

    public Nutrient(String name) {
        this.name = name;
    }
}
