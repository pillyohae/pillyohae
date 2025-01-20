package com.example.pillyohae.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 추천 키워드 DTO
 *
 * @see com.example.pillyohae.recommendation.service.RecommendationService#create(String, Long)
 */
@Getter
@NoArgsConstructor
public class RecommendationKeywordDto {

    private String recommendation;

    public RecommendationKeywordDto(String recommendation) {
        this.recommendation = recommendation;
    }
}
