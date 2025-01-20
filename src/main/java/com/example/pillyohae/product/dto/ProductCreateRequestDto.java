package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ProductCreateRequestDto {
    @NotBlank
    private String productName;

    @NotBlank
    private String category;

    @NotBlank
    private String description;

    @NotBlank
    private String companyName;

    @NotNull
    @Positive
    private Long price;

    private String imageUrl;


    private ProductStatus status = ProductStatus.SELLING;

    //테스트 코드용
    public ProductCreateRequestDto(String productName, String category, String description, String companyName, Long price, ProductStatus status) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
    }

    public Product toEntity(User user) {
        return new Product(
            user,
            this.productName,
            this.category,
            this.description,
            this.companyName,
            this.price,
            this.status = ProductStatus.SELLING
        );
    }
}
