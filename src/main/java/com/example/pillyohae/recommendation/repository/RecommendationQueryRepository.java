package com.example.pillyohae.recommendation.repository;

import com.example.pillyohae.recommendation.dto.RecommendationQueryResponseDto;
import java.util.List;

public interface RecommendationQueryRepository {

    List<RecommendationQueryResponseDto> findBySurveyId(Long surveyId);

}
