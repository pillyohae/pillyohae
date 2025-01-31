package com.example.pillyohae.product.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "nutrients")
public class Nutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nutrientId;

    private String name;

    private String description;

//    @OneToMany(mappedBy = "nutrient", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ProductNutrient> productNutrients = new ArrayList<>();

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
