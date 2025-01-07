package com.example.pillyohae.product.entity;

import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private String category;

    private String description;

    private String companyName;

    private Long price;

    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Product(User user, String productName, String category, String description, String companyName, Long price, String imageUrl, ProductStatus status) {
        this.user = user;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public void updateProduct(String productName, String category, String description, String companyName, Long price, String imageUrl, ProductStatus status) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public void deleteProduct() {
        this.status = ProductStatus.DELETED;
    }

    public Product() {

    }
}
