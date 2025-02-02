package com.example.pillyohae.product.dto.product;

import com.example.pillyohae.product.entity.PersonaMessage;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.type.ProductStatus;
import lombok.Getter;

import java.util.List;

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
    private List<String> personaMessages;

    public ProductCreateResponseDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.category = product.getCategory().getName();
        this.description = product.getDescription();
        this.companyName = product.getCompanyName();
        this.price = product.getPrice();
        this.status = product.getStatus();
        this.stock = product.getStock();
        this.nutrientName = product.getNutrient().getName();
        this.personaMessages =
            product.getPersonaMessages()
                .stream()
                .map(PersonaMessage::getMessage)
                .toList();
    }
}
