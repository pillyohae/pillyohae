package com.example.main.product.dto.product;

import com.example.common.product.entity.Category;
import com.example.common.product.entity.Nutrient;
import com.example.common.product.entity.Product;
import com.example.common.product.entity.type.ProductStatus;
import com.example.common.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ProductCreateRequestDto {
    @NotBlank
    private String productName;

    @NotNull
    private Long categoryId;

    @NotBlank
    private String description;

    @NotBlank
    private String companyName;

    @NotNull
    @Positive
    private Long price;

    private ProductStatus status = ProductStatus.SELLING; // 테스트 코드 때문에 삭제x -> 테스트 코드에서 status 상수 처리하고 삭제할 예정

    @NotNull
    @Positive
    private Integer stock;

    @NotNull
    private Long nutrientId;

    //테스트 코드용
    public ProductCreateRequestDto(String productName, Long categoryId, String description, String companyName, Long price, ProductStatus status) {
        this.productName = productName;
        this.categoryId = categoryId;
        this.description = description;
        this.companyName = companyName;
        this.price = price;
        this.status = status;
    }

    public Product toEntity(User user, Nutrient nutrient, Category category) {
        return new Product(
            user,
            this.productName,
            category,
            this.description,
            this.companyName,
            this.price,
            this.stock,
            nutrient
        );
    }
}
