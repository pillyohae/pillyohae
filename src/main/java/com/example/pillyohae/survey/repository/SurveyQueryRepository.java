package com.example.pillyohae.survey.repository;

import com.example.pillyohae.survey.dto.SurveyResponseDto;
import java.util.List;

public interface SurveyQueryRepository {

    List<SurveyResponseDto> findAllByUserId(Long userId);

}
