package com.example.pillyohae.product.dto;

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
