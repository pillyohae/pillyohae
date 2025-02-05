package com.example.pillyohae.survey.controller;

import com.example.pillyohae.survey.dto.SurveyDetailsDto;
import com.example.pillyohae.survey.dto.SurveyResponseDto;
import com.example.pillyohae.survey.dto.SurveySubmitRequestDto;
import com.example.pillyohae.survey.dto.SurveySubmitResponseDto;
import com.example.pillyohae.survey.service.SurveyService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    /**
     * 사용자의 설문 응답 저장
     *
     * @param userDetails 사용자 정보
     * @param requestDto  설문 응답 데이터
     * @return 정상 처리 시 DTO
     */
    @PostMapping
    public ResponseEntity<SurveySubmitResponseDto> submit(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody SurveySubmitRequestDto requestDto
    ) {

        return ResponseEntity.ok(surveyService.create(userDetails.getUsername(), requestDto));
    }

    /**
     * 사용자의 설문 내역 조회
     *
     * @param userDetails 사용자 정보
     * @return 정상 처리 시 DTO
     */
    @GetMapping
    public ResponseEntity<List<SurveyResponseDto>> findSurvey(
        @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(surveyService.findSurveys(userDetails.getUsername()));
    }

    /**
     * 사용자의 설문 내역 삭제
     *
     * @param userDetails 사용자 정보
     * @param surveyId    설문 ID
     * @return 정상 처리 시 200 OK
     */
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<Void> deleteSurvey(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long surveyId
    ) {

        surveyService.deleteById(userDetails.getUsername(), surveyId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 사용자의 설문 관련 상세 정보 조회
     */
    @GetMapping("/{surveyId}/details")
    public ResponseEntity<SurveyDetailsDto> findSurveyReason(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long surveyId
    ) {
        SurveyDetailsDto reason = surveyService.findSurveyWithDetails(
            userDetails.getUsername(), surveyId);

        return new ResponseEntity<>(reason, HttpStatus.OK);
    }
}
