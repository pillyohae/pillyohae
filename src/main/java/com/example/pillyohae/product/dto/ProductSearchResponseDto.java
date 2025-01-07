package com.example.pillyohae.product.dto;

import lombok.Getter;

@Getter
public class ProductSearchResponseDto {
    private Long productId;
    private String productName;
    private String companyName;
    private String category;

    public ProductSearchResponseDto(Long productId, String productName, String companyName, String category) {
        this.productId = productId;
        this.productName = productName;
        this.companyName = companyName;
        this.category = category;
    }

}
