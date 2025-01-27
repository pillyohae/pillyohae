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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        String prompt = createRecommendationPrompt(survey.getCategories());
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
     * @param surveyCategories 사용자 관심사
     * @return 추천 프롬프트
     */
    private String createRecommendationPrompt(String surveyCategories) {

        return String.format(
            "사용자가 건강 관심사를 나열하면, 각각에 부합하는 보충제를 JSON 형식으로 반환하세요. 응답은 항상 \"recommendation\"키를 포함하는 JSON 객체의 배열 형식이어야 합니다. 관심사는 쉼표로 구분되며, 각 관심사에 대한 추천 보충제는 \"recommendation\" 키에 연결됩니다."
                + "또한 응답에는 코드블럭, 개행, 공백을 제거해주세요." + "메시지 형태: 관심사1, 관심사2, 관심사3" + " 응답 형태: "
                + "[{\"recommendation\":\"보충제1\"},{\"recommendation\":\"보충제2\"},{\"recommendation\":\"보충제3\"}]"
                + "메시지: "
                + surveyCategories);
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
            ChatResponse response = chatModel.call(new Prompt(userMessage,
                OpenAiChatOptions.builder().model(ChatModel.GPT_4_O.getValue()).build()));

            String result = response.getResult().getOutput().getText().replace(" ", "");

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(result, new TypeReference<>() {
            });

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
