package com.example.pillyohae.product.dto.product;

import com.example.pillyohae.product.entity.PersonaMessage;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.Getter;

import java.util.List;

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
    private List<String> personaMessages;

    public ProductSearchResponseDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.companyName = product.getCompanyName();
        this.category = product.getCategory().getName(); // 올바른 값 할당
        this.price = product.getPrice();
        this.status = product.getStatus();
        this.stock = product.getStock();
        this.thumbnailImage = product.getThumbnailUrl();
        this.personaMessages =
            product.getPersonaMessages()
                .stream()
                .map(PersonaMessage::getMessage)
                .toList();
    }
}
