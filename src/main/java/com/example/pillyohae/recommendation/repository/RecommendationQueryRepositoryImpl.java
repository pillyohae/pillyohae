package com.example.pillyohae.recommendation.repository;

import static com.example.pillyohae.recommendation.entity.QRecommendation.recommendation;

import com.example.pillyohae.recommendation.dto.QRecommendationQueryResponseDto;
import com.example.pillyohae.recommendation.dto.RecommendationQueryResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public class RecommendationQueryRepositoryImpl implements RecommendationQueryRepository {

    private final JPAQueryFactory queryFactory;

    RecommendationQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 설문 ID로 추천 상품 조회
     *
     * @param surveyId 설문 ID
     * @return 추천 상품 조회 응답 DTO 리스트
     */
    @Override
    public List<RecommendationQueryResponseDto> findBySurveyId(Long surveyId) {
        return queryFactory.select(new QRecommendationQueryResponseDto(
                recommendation.product.productId,
                recommendation.product.productName,
                recommendation.product.imageUrl,
                recommendation.product.price
            ))
            .from(recommendation)
            .where(recommendation.survey.id.eq(surveyId))
            .fetch();
    }

}
