package com.example.pillyohae.product.dto.image;

import com.example.pillyohae.product.entity.ProductImage;
import lombok.Getter;

@Getter
public class UpdateImageResponseDto {

    private final Long imageId;
    private final Integer position;

    public UpdateImageResponseDto(Long id, Integer position) {
        this.imageId = id;
        this.position = position;
    }

    public static UpdateImageResponseDto toDto(ProductImage productImage) {
        return new UpdateImageResponseDto(
            productImage.getId(),
            productImage.getPosition()
        );
    }
}
