package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.Getter;

@Getter
public class ProductSearchResponseDto {
    private Long productId;
    private String productName;
    private String companyName;
    private String category;
    private Long price;
    private ProductStatus status;

    public ProductSearchResponseDto(Long productId, String productName, String companyName, String category, Long price) {
        this.productId = productId;
        this.productName = productName;
        this.companyName = companyName;
        this.category = category;
        this.price = price;
    }

    public ProductSearchResponseDto(Long productId, String productName, String companyName, String category, Long price, ProductStatus status) {
        this.productId = productId;
        this.productName = productName;
        this.companyName = companyName;
        this.category = category;
        this.price = price;
        this.status = status;
    }

}
