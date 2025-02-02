package com.example.pillyohae.product.dto.product;

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
    private Integer stock;
    private String thumbnailImage;

    public ProductSearchResponseDto(Long productId, String productName, String companyName, String category, Long price, ProductStatus status, Integer stock, String thumbnailImage) {
        this.productId = productId;
        this.productName = productName;
        this.companyName = companyName;
        this.category = category;
        this.price = price;
        this.status = status;
        this.stock = stock;
        this.thumbnailImage = thumbnailImage;
    }
}
