package com.example.pillyohae.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RecommendationResponseDto {

    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Long price;

    @Builder
    public RecommendationResponseDto(Long productId, String productName, String imageUrl, Long price) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
    }

}
