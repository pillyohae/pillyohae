package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.Category;
import com.example.pillyohae.product.entity.Nutrient;
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
    private ProductStatus status;
    private Integer stock;
    private String nutrientName;

    public ProductCreateResponseDto(Long productId, String productName, Category category, String description, String companyName, Long price, ProductStatus status, Integer stock, Nutrient nutrient) {
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
