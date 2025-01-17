package com.example.pillyohae.survey.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 설문 제출 요청 DTO
 *
 * @see com.example.pillyohae.survey.controller.SurveyController#submit
 */
@Getter
@RequiredArgsConstructor
public class SurveySubmitRequestDto {

    @NotBlank(message = "설문 응답을 반드시 포함해주세요.")
    private final String categories;
}
