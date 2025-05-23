package com.example.main.product.dto.image;

import lombok.Getter;

@Getter
public class UpdateImageRequestDto {

    private final Long imageId;
    private final Integer position;

    public UpdateImageRequestDto(Long imageId, Integer position) {
        this.imageId = imageId;
        this.position = position;
    }
}
