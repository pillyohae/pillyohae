package com.example.pillyohae.product.entity;

import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
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

    private String description;

    private String companyName;

    private Long price;

    private Integer stock;

    @Enumerated(value = EnumType.STRING)
    private ProductStatus status = ProductStatus.SELLING;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrient_nutrientId")
    private Nutrient nutrient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_categoryId")
    private Category category;

    private LocalDateTime deletedAt;

    public Product(User user, String productName, Category category, String description, String companyName, Long price, Integer stock, Nutrient nutrient) {
        this.user = user;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.stock = stock;
        this.nutrient = nutrient;
    }

    public Product(User user, String productName, Category category, String description, String companyName, Long price, ProductStatus status) {
        this.user = user;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
    }


    public void updateProduct(String productName, Category category, String description, String companyName, Long price, Integer stock, Nutrient nutrient) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.stock = stock;
        this.nutrient = nutrient;

    }

    public void deleteProduct() {

        this.status = ProductStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public String getThumbnailUrl() {
        // 이미지가 존재하는 경우
        if (images != null && !images.isEmpty()) {
            // position 0 이미지가 있으면 이를 썸네일로 설정
            String thumbnailUrl = images.stream()
                .filter(image -> image.getPosition() == 0) // position 0인 이미지를 찾음
                .map(ProductImage::getFileUrl)
                .findFirst()
                .orElse(null); // position 0이 없으면 null 리턴

            // position 0이 없다면 position 1 이미지를 썸네일로 설정
            if (thumbnailUrl == null) {
                thumbnailUrl = images.stream()
                    .filter(image -> image.getPosition() == 1) // position 1인 이미지를 찾음
                    .map(ProductImage::getFileUrl)
                    .findFirst()
                    .orElse(null); // position 1이 없다면 null 리턴
            }

            return thumbnailUrl;
        }
        return null;
    }

    public Integer deductStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new CustomResponseStatusException(ErrorCode.QUANTITY_CANNOTBE_NEGATIVE);
        }
        if (this.stock < quantity) {
            throw new CustomResponseStatusException(ErrorCode.LACK_OF_STOCK);
        }
        this.stock -= quantity; // 재고 차감

        return this.stock; // 차감 후 남은 재고 반환
    }

    public Product() {

    }
}
