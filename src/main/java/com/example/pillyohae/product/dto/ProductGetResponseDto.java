package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductGetResponseDto {
    private Long productId;
    private String productName;
    private String category;
    private String description;
    private String companyName;
    private Long price;
    private ProductStatus status;
    private List<String> imageUrls;

    public ProductGetResponseDto(Long productId, String productName, String category, String description, String companyName, Long price, ProductStatus status, List<String> imageUrls) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
        this.imageUrls = imageUrls;
    }
}
