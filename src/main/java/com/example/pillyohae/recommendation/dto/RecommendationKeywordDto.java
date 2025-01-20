package com.example.pillyohae.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendationKeywordDto {

    private String recommendation;

    public RecommendationKeywordDto(String recommendation) {
        this.recommendation = recommendation;
    }
}
