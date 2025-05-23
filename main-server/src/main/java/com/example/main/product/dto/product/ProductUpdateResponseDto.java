package com.example.main.product.dto.product;

import com.example.common.product.entity.Category;
import com.example.common.product.entity.Nutrient;
import com.example.common.product.entity.type.ProductStatus;
import lombok.Getter;

@Getter
public class ProductUpdateResponseDto {

    private Long productId;
    private String productName;
    private String category;
    private String description;
    private String companyName;
    private Long price;
    private ProductStatus status;
    private Integer stock;
    private String nutrientName;

    public ProductUpdateResponseDto(Long productId, String productName, Category category, String description, String companyName, Long price, ProductStatus status, Integer stock, Nutrient nutrient) {
        this.productId = productId;
        this.productName = productName;
        this.category = category.getName();
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
        this.stock = stock;
        this.nutrientName = nutrient.getName();
    }
}
