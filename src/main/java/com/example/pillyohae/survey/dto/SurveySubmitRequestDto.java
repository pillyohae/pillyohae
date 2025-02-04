package com.example.pillyohae.survey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 설문 제출 요청 DTO
 *
 * @see com.example.pillyohae.survey.controller.SurveyController#submit
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveySubmitRequestDto {

    @NotBlank(message = "성별을 입력해주세요.")
    private String gender;

    @NotNull(message = "나이를 입력해주세요.")
    private Integer age;

    @NotBlank(message = "키를 입력해주세요.")
    private String height;

    @NotBlank(message = "몸무게를 입력해주세요.")
    private String weight;

    // 건강 목표 및 상태
    // 프론트에서는 healthGoals 배열과 healthCondition 문자열을 보냄
    private List<String> healthGoals;
    private String healthCondition;

    // 생활습관 정보 (예: { exercise, smoking, sleepQuality, stressLevel })
    private Map<String, String> lifestyle;
}
