package com.example.pillyohae.recommendation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResultWrapper {

    private List<String> recommendedProducts;
    private String recommendationReason;
}
