package com.example.main.survey.repository;

import com.example.main.survey.dto.SurveyResponseDto;
import java.util.List;

public interface SurveyQueryRepository {

    List<SurveyResponseDto> findAllByUserId(Long userId);

}
