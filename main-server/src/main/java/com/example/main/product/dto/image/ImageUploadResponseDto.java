package com.example.main.product.dto.image;

import lombok.Getter;

@Getter
public class ImageUploadResponseDto {
    Long imageId;
    Integer position;
    String fileUrl;
    String fileKey;

    public ImageUploadResponseDto(Long imageId, Integer position, String fileUrl, String fileKey) {
        this.imageId = imageId;
        this.position = position;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
    }
}
