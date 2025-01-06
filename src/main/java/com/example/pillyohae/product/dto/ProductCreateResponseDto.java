package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.Getter;

@Getter
public class ProductCreateResponseDto {

    private Long productId;
    private String productName;
    private String category;
    private String description;
    private String companyName;
    private Long price;
    private String imageUrl;
    private ProductStatus status;

    public ProductCreateResponseDto(Long productId, String productName, String category, String description, String companyName, Long price, String imageUrl, ProductStatus status) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = status;
    }
}
