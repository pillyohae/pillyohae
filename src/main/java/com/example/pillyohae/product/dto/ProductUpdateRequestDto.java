package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.Getter;

@Getter
public class ProductUpdateRequestDto {

    private String productName;

    private Long categoryId;

    private String description;

    private String companyName;

    private Long price;

    private ProductStatus status;

    private Integer stock;

    private Long nutrientId;
}
