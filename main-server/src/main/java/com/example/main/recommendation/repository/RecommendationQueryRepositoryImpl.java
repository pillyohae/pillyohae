package com.example.main.recommendation.repository;

import com.example.common.product.entity.QProduct;
import com.example.main.recommendation.dto.QRecommendationQueryResponseDto;
import com.example.main.recommendation.dto.RecommendationQueryResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.example.common.product.entity.QProductImage.productImage;
import static com.example.common.recommendation.entity.QRecommendation.recommendation;

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
                productImage.fileUrl,
                recommendation.product.price
            ))
            .from(recommendation)
            .leftJoin(productImage)
            .on(productImageJoinCondition(recommendation.product))
            .where(recommendation.survey.id.eq(surveyId)

            )
            .fetch();
    }

    private BooleanExpression productImageJoinCondition(QProduct product) {
        return productImage.product.productId.eq(product.productId)
            .and(productImage.position.eq(0)
                .or(productImage.position.eq(1)
                    .and(thumbnailExists(product)))
            );
    }

    private BooleanExpression thumbnailExists(QProduct product) {
        return JPAExpressions.select(productImage.fileUrl)
            .from(productImage)
            .where(productImage.product.productId.eq(product.productId)
                .and(productImage.position.eq(0))
            )
            .notExists();
    }

}
