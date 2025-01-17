package com.example.pillyohae.survey.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 설문 내역 DTO
 *
 * @see com.example.pillyohae.survey.repository.SurveyQueryRepository#findAllByUserId
 */
@Getter
public class SurveyResponseDto {

    private Long surveyId;

    private String categories;

    private LocalDateTime createAt;

    @QueryProjection
    public SurveyResponseDto(Long surveyId, String categories, LocalDateTime createAt) {
        this.surveyId = surveyId;
        this.categories = categories;
        this.createAt = createAt;
    }
}
