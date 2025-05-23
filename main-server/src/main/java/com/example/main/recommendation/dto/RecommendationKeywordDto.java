package com.example.main.recommendation.dto;

import com.example.main.recommendation.service.RecommendationService;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 추천 키워드 DTO
 *
 * @see RecommendationService#create(String, Long)
 */
@Getter
@NoArgsConstructor
public class RecommendationKeywordDto {

    private String recommendation;

    public RecommendationKeywordDto(String recommendation) {
        this.recommendation = recommendation;
    }
}
