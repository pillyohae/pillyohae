package com.example.main.survey.dto;

import com.example.common.survey.entity.Survey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class SurveyDetailsDto {

    private final String gender;

    private final Integer age;

    private final String height;

    private final String weight;

    private final String healthGoals;

    private final String healthCondition;

    private final Map<String, String> lifestyle;

    private final String recommendationReason;

    public static SurveyDetailsDto toDto(Survey survey) {
        return new SurveyDetailsDto(
            survey.getGender(), survey.getAge(),
            survey.getHeight(), survey.getWeight(),
            survey.getHealthGoals(), survey.getHealthCondition(),
            parseLifestyle(survey.getLifestyle()), survey.getRecommendationReason()
        );
    }

    public static Map<String, String> parseLifestyle(String lifestyle) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // `{exercise=주 3회, smoking=흡연 O, ...}` 같은 Map.toString() 형식 처리
            if (lifestyle.startsWith("{") && lifestyle.contains("=")) {
                return convertStringToMap(lifestyle);
            }

            // 정상적인 JSON이면 Jackson으로 변환
            return objectMapper.readValue(lifestyle, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("lifestyle JSON 변환 실패: " + lifestyle, e);
        }
    }

    /**
     * {key=value, key2=value2}` 형태를 JSON으로 변환
     */
    private static Map<String, String> convertStringToMap(String str) {
        Map<String, String> map = new HashMap<>();
        str = str.substring(1, str.length() - 1); // `{}` 제거
        String[] entries = str.split(", ");

        for (String entry : entries) {
            String[] keyValue = entry.split("=", 2); // `=` 기준으로 key-value 분리
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }
}
