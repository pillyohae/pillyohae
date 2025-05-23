package com.example.main.survey.dto;

import java.time.LocalDateTime;

import com.example.main.survey.controller.SurveyController;
import lombok.Getter;

/**
 * 사용자 설문 제출 응답 DTO
 *
 * @see SurveyController#submit
 */
@Getter
public class SurveySubmitResponseDto {

    private final Long surveyId;

    private final String categories;

    private final LocalDateTime createAt;

    public SurveySubmitResponseDto(Long surveyId, String categories, LocalDateTime createAt) {
        this.surveyId = surveyId;
        this.categories = categories;
        this.createAt = createAt;
    }
}
