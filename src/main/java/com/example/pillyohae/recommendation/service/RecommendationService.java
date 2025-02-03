package com.example.pillyohae.recommendation.service;

import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.service.ProductService;
import com.example.pillyohae.recommendation.dto.RecommendationCreateResponseDto;
import com.example.pillyohae.recommendation.dto.RecommendationKeywordDto;
import com.example.pillyohae.recommendation.dto.RecommendationQueryResponseDto;
import com.example.pillyohae.recommendation.entity.Recommendation;
import com.example.pillyohae.recommendation.repository.RecommendationRepository;
import com.example.pillyohae.survey.entity.Survey;
import com.example.pillyohae.survey.service.SurveyService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    private final OpenAiChatModel chatModel;
    private final SurveyService surveyService;
    private final ProductService productService;

    /**
     * 추천 상품 생성
     *
     * @param email    사용자 이메일
     * @param surveyId 설문 ID
     * @return 추천 상품 목록
     */
    public List<RecommendationCreateResponseDto> create(String email, Long surveyId) {

        Survey survey = surveyService.findSurvey(email, surveyId);

        // 1. 프롬프트 생성
        String prompt = createRecommendationPrompt(survey);
        log.info("prompt : {}", prompt);

        // 2. 추천 상품 키워드 생성
        RecommendationKeywordDto[] keywords = createRecommendationKeyword(prompt);

        // 3. 키워드로 상품 조회
        List<Product> recommendationProducts = productService.findByNameLike(keywords);
        log.info("recommendationProducts : {}", recommendationProducts.size());

        if (recommendationProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "추천 상품을 찾을 수 없습니다.");
        }

        // 4. DB 저장 및 결과 반환
        List<Recommendation> savedRecommendations = new ArrayList<>();
        for (Product product : recommendationProducts) {
            savedRecommendations.add(new Recommendation(survey, product));
        }

        recommendationRepository.saveAll(savedRecommendations);

        List<RecommendationCreateResponseDto> responseDtoList = new ArrayList<>();
        for (Recommendation recommendation : savedRecommendations) {
            responseDtoList.add(RecommendationCreateResponseDto.builder()
                .productId(recommendation.getProduct().getProductId())
                .productName(recommendation.getProduct().getProductName())
                .imageUrl(recommendation.getProduct().getThumbnailUrl())
                .price(recommendation.getProduct().getPrice())
                .build());
        }

        return responseDtoList;
    }

    /**
     * 추천 프롬프트 생성 JSON 형식의 String 으로 반환
     *
     * @param
     * @return 추천 프롬프트
     */
    private String createRecommendationPrompt(Survey survey) {
        return String.format(
            "당신은 전문적인 영양제 추천 AI입니다. 아래 사용자 정보를 바탕으로 최적의 영양제 이름을 최대 3개 추천하세요. " +
                "추천 결과는 오직 영양제 이름만을 콤마(,)로 구분하여 출력해 주세요. " +
                "사용자 정보:\n" +
                "- 연령: %d세\n" +
                "- 성별: %s\n" +
                "- 신장: %s\n" +
                "- 몸무게: %s\n" +
                "- 건강 목표: %s\n" +
                "- 건강 상태: %s\n" +
                "- 생활 습관: %s\n" +
                "예시 출력: 오메가3,비타민B군,L-카르니틴",
            survey.getAge(),
            survey.getGender(),
            survey.getHeight(),
            survey.getWeight(),
            survey.getHealthGoals(),
            survey.getHealthCondition(),
            survey.getLifestyle()
        );
    }


    /**
     * 추천 상품 키워드 생성
     * <p>Json 매핑 오류 혹은 API 호출 실패 시 500 에러 반환</p>
     *
     * @param prompt 요청 프롬프트
     * @return 추천 상품 키워드
     */
    private RecommendationKeywordDto[] createRecommendationKeyword(String prompt) {
        UserMessage userMessage = new UserMessage(prompt);
        try {
            ChatResponse response = chatModel.call(new Prompt(
                userMessage,
                OpenAiChatOptions.builder().model(ChatModel.GPT_4_O.getValue()).build()
            ));

            // AI 응답 예시: "오메가3,비타민B군,L-카르니틴"
            String result = response.getResult().getOutput().getText().trim();
            log.info("result : {}", result);

            // 콤마로 분리 (양쪽 공백 제거)
            String[] parts = result.split("\\s*,\\s*");
            RecommendationKeywordDto[] dtos = new RecommendationKeywordDto[parts.length];
            for (int i = 0; i < parts.length; i++) {
                dtos[i] = new RecommendationKeywordDto(parts[i]);
            }
            return dtos;
        } catch (Exception e) {
            log.error("추천 상품 키워드 생성에 실패했습니다.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "추천 상품 키워드 생성에 실패했습니다.");
        }
    }


    /**
     * 추천 상품 조회
     *
     * @param email    사용자 이메일
     * @param surveyId 설문 ID
     * @return 추천 상품 조회 응답 DTO 리스트
     */
    public List<RecommendationQueryResponseDto> getRecommendations(String email, Long surveyId) {

        Survey findSurvey = surveyService.findSurvey(email, surveyId);

        return recommendationRepository.findBySurveyId(findSurvey.getId());
    }

}
