package com.example.pillyohae.product.dto;

import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.user.entity.User;
import lombok.Getter;

@Getter
public class ProductCreateRequestDto {
    private String productName;
    private String category;
    private String description;
    private String companyName;
    private Long price;
    private String imageUrl;
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
