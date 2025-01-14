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

    @NotNull
    private ProductStatus status;

    public Product toEntity(User user) {
        return new Product(
            user,
            this.productName,
            this.category,
            this.description,
            this.companyName,
            this.price,
            this.status
        );
    }
}
