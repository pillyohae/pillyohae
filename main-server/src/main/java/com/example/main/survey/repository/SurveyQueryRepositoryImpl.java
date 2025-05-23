package com.example.main.survey.repository;

import com.example.common.survey.entity.QSurvey;
import com.example.main.survey.dto.QSurveyResponseDto;
import com.example.main.survey.dto.SurveyResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class SurveyQueryRepositoryImpl implements SurveyQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QSurvey survey = QSurvey.survey;

    SurveyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 사용자의 설문 내역 조회
     *
     * @param userid 사용자 ID
     * @return 설문 내역 DTO List
     */
    @Override
    public List<SurveyResponseDto> findAllByUserId(Long userid) {

        return queryFactory.select(
                new QSurveyResponseDto(survey.id, survey.healthGoals, survey.createdAt))
            .from(survey)
            .where(survey.user.id.eq(userid))
            .fetch();
    }

}
