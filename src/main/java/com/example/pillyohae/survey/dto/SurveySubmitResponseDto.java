package com.example.pillyohae.survey.dto;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 사용자 설문 제출 응답 DTO
 *
 * @see com.example.pillyohae.survey.controller.SurveyController#submit
 */
@Getter
public class SurveySubmitResponseDto {

    private Long surveyId;

    private String categories;

    private LocalDateTime createAt;

    public SurveySubmitResponseDto(Long surveyId, String categories, LocalDateTime createAt) {
        this.surveyId = surveyId;
        this.categories = categories;
        this.createAt = createAt;
    }
}
