package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Integer stock;
    private List<ImageResponseDto> images;

    public ProductGetResponseDto(Long productId, String productName, String category, String description,
                                 String companyName, Long price, ProductStatus status, Integer stock,
                                 List<ImageResponseDto> images) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
        this.stock = stock;
        this.images = images;
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
