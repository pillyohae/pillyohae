package com.example.pillyohae.survey.service;

import com.example.pillyohae.survey.dto.SurveyResponseDto;
import com.example.pillyohae.survey.dto.SurveySubmitRequestDto;
import com.example.pillyohae.survey.dto.SurveySubmitResponseDto;
import com.example.pillyohae.survey.entity.Survey;
import com.example.pillyohae.survey.repository.SurveyRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final UserService userService;

    /**
     * 설문 응답을 DB에 등록
     *
     * @param email      사용자 이메일
     * @param requestDto 설문 응답 DTO
     * @return 정상 처리 DTO
     */
    @Transactional
    public SurveySubmitResponseDto create(String email, SurveySubmitRequestDto requestDto) {

        User user = userService.findByEmail(email);

        Survey savedSurvey = new Survey(user, requestDto.getCategories());

        surveyRepository.save(savedSurvey);

        return new SurveySubmitResponseDto(savedSurvey.getId(), savedSurvey.getCategories(), savedSurvey.getCreatedAt());
    }

    /**
     * 사용자의 설문 내역 조회
     *
     * @param email 사용자 이메일
     * @return 정상 처리 시 응답 DTO
     */
    public List<SurveyResponseDto> findSurveys(String email) {

        User user = userService.findByEmail(email);

        List<SurveyResponseDto> findSurveys = surveyRepository.findAllByUserId(user.getId());

        if (findSurveys.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 설문 내역을 찾을 수 없습니다.");
        }

        return findSurveys;

    }

    /**
     * 설문 단건 조회
     *
     * @param email    사용자 이메일
     * @param surveyId 설문 ID
     * @return 정상 처리 DTO
     */
    public SurveyResponseDto findById(String email, Long surveyId) {

        User user = userService.findByEmail(email);

        Survey findSurvey = surveyRepository.findById(surveyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 설문 내역을 찾을 수 없습니다."));

        if (!user.getId().equals(findSurvey.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        return new SurveyResponseDto(findSurvey.getId(), findSurvey.getCategories(), findSurvey.getCreatedAt());
    }

    /**
     * 설문 내역 단건 삭제
     *
     * @param email 사용자 이메일
     */
    @Transactional
    public void deleteById(String email, Long surveyId) {

        User user = userService.findByEmail(email);

        Survey findSurvey = surveyRepository.findById(surveyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 설문 내역을 찾을 수 없습니다."));

        if (!user.getId().equals(findSurvey.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        surveyRepository.delete(findSurvey);
    }
}
