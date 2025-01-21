package com.example.pillyohae.product.entity;

import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private ProductStatus status = ProductStatus.SELLING;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    private LocalDateTime deletedAt;

    public Product(User user, String productName, String category, String description, String companyName, Long price) {
        this.user = user;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = ProductStatus.SELLING; // 기본값 설정
    }

    public Product(User user, String productName, String category, String description, String companyName, Long price, ProductStatus status) {
        this.user = user;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
    }

    public void updateProduct(String productName, String category, String description, String companyName, Long price, ProductStatus status) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
    }

    public void deleteProduct() {

        this.status = ProductStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public Product() {

    }
}
