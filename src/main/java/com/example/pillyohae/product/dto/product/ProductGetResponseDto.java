package com.example.pillyohae.product.dto.product;

import com.example.pillyohae.product.dto.category.CategoryResponseDto;
import com.example.pillyohae.product.dto.nutrient.NutrientResponseDto;
import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class ProductGetResponseDto {

    private Long productId;
    private String productName;
    private CategoryResponseDto category;
    private String description;
    private String companyName;
    private Long price;
    private ProductStatus status;
    private Integer stock;
    private List<ImageResponseDto> images;
    private NutrientResponseDto nutrient;

    public ProductGetResponseDto(Long productId, String productName, CategoryResponseDto category, String description,
                                 String companyName, Long price, ProductStatus status, Integer stock,
                                 List<ImageResponseDto> images, NutrientResponseDto nutrient) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
        this.stock = stock;
        this.images = images;
        this.nutrient = nutrient;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageResponseDto {

        private Long imageId;
        private String imageUrl;
        private Integer position;


    }
}
