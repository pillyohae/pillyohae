package com.example.main.recommendation.repository;

import com.example.main.recommendation.dto.RecommendationQueryResponseDto;
import java.util.List;

public interface RecommendationQueryRepository {

    List<RecommendationQueryResponseDto> findBySurveyId(Long surveyId);

}
