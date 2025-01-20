package com.example.pillyohae.recommendation.controller;

import com.example.pillyohae.recommendation.dto.RecommendationResponseDto;
import com.example.pillyohae.recommendation.service.RecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys/{surveyId}/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 추천 상품 생성 요청
     *
     * @param userDetails 사용자 정보
     * @param surveyId    설문 ID
     * @return 추천 상품 목록
     */
    @PostMapping
    public ResponseEntity<List<RecommendationResponseDto>> generate(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long surveyId
    ) throws JsonProcessingException {

        // 추천 상품 생성
        return ResponseEntity.ok(recommendationService.create(userDetails.getUsername(), surveyId));
    }

}
