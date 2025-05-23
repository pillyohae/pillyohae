package com.example.main.recommendation.controller;

import com.example.main.recommendation.dto.RecommendationCreateResponseDto;
import com.example.main.recommendation.dto.RecommendationQueryResponseDto;
import com.example.main.recommendation.service.RecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<List<RecommendationCreateResponseDto>> generate(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long surveyId
    ) {

        return new ResponseEntity<>(recommendationService.create(userDetails.getUsername(), surveyId), HttpStatus.CREATED);
    }

    /**
     * 추천 상품 조회
     *
     * @param userDetails 사용자 정보
     * @param surveyId    설문 ID
     * @return 추천 상품 목록
     */
    @GetMapping
    public ResponseEntity<List<RecommendationQueryResponseDto>> getRecommendation(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long surveyId
    ) {
        return ResponseEntity.ok(recommendationService.getRecommendations(userDetails.getUsername(), surveyId));
    }

}
