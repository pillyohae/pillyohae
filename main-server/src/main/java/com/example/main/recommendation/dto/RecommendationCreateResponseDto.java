package com.example.main.recommendation.dto;

import com.example.main.recommendation.service.RecommendationService;
import lombok.Builder;
import lombok.Getter;

/**
 * 추천 상품 응답 DTO
 *
 * @see RecommendationService#create(String, Long)
 */
@Getter
public class RecommendationCreateResponseDto {

    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Long price;

    @Builder
    public RecommendationCreateResponseDto(Long productId, String productName, String imageUrl,
        Long price) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
    }


}
