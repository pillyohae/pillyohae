package com.example.main.recommendation.dto;

import com.example.main.recommendation.repository.RecommendationQueryRepositoryImpl;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * 추천 상품 조회 응답 DTO
 *
 * @see RecommendationQueryRepositoryImpl#findBySurveyId(Long)
 */
@Getter
public class RecommendationQueryResponseDto {

    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Long price;

    @QueryProjection
    public RecommendationQueryResponseDto(Long productId, String productName, String imageUrl, Long price) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
    }
}
