package com.example.pillyohae.recommendation.service;

import com.example.pillyohae.product.dto.product.ProductRecommendationDto;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.service.ProductService;
import com.example.pillyohae.recommendation.dto.RecommendationCreateResponseDto;
import com.example.pillyohae.recommendation.dto.RecommendationQueryResponseDto;
import com.example.pillyohae.recommendation.dto.RecommendationResultWrapper;
import com.example.pillyohae.recommendation.entity.Recommendation;
import com.example.pillyohae.recommendation.repository.RecommendationRepository;
import com.example.pillyohae.survey.entity.Survey;
import com.example.pillyohae.survey.service.SurveyService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        List<ProductRecommendationDto> dtos = productService.getAllProductsWithNutrient();

        // 1. 프롬프트 생성 (상품 목록 dtos 포함)
        String prompt = createRecommendationPrompt(survey, dtos);
        log.info("prompt : {}", prompt);

        // 2. AI 응답 파싱: 추천 결과 및 전체 추천 이유
        RecommendationResultWrapper resultWrapper = createRecommendationResult(prompt);
        List<String> recommendedProductNames = resultWrapper.getRecommendedProducts();
        String overallRecommendationReason = resultWrapper.getRecommendationReason();

        // 3. 키워드(추천된 상품 이름들)로 상품 조회
        List<Product> recommendationProducts = productService.findByNameLike(
            recommendedProductNames);
        log.info("추천 상품 조회 개수 : {}", recommendationProducts.size());

        if (recommendationProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "추천 상품을 찾을 수 없습니다.");
        }

        // 4. Survey에 전체 추천 이유 저장 (데이터 중복 방지)
        survey.updateRecommendationReason(overallRecommendationReason);

        // 5. 추천 상품 저장 (Recommendation 엔티티에서는 추천 이유를 제외)
        List<Recommendation> savedRecommendations = new ArrayList<>();
        for (Product product : recommendationProducts) {
            savedRecommendations.add(new Recommendation(survey, product));
        }
        recommendationRepository.saveAll(savedRecommendations);

        // 6. 응답 DTO 구성: 각 추천 상품 정보와 함께 Survey의 추천 이유를 포함하여 반환
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
    private String createRecommendationPrompt(Survey survey, List<ProductRecommendationDto> dtos) {
        // dtos를 JSON 문자열로 변환 (예: [{"productName": "수정 테스트"}, {"productName": "오메가3"}, ...])
        String dtosJson;
        try {
            dtosJson = new ObjectMapper().writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("상품 목록 JSON 변환 실패", e);
        }
        return String.format(
            "당신은 전문적인 영양제 추천 AI입니다. 아래 사용자 정보와 게시된 상품 목록을 바탕으로, " +
                "최적의 영양제 이름을 최대 3개 추천하고, 왜 이러한 추천을 했는지 전체 추천 이유를 최소 3줄짜리 문장으로 상세하게 작성해 주세요. " +
                "반드시 JSON 형식으로 응답해야 하며, 여기에 코드 블록(```json` 등)은 포함하지 마세요. " +
                "반드시 두 개의 키 'recommendedProducts'와 'recommendationReason'를 포함하는 JSON 객체를 반환하세요.\n\n"
                +
                "사용자 정보:\n" +
                "- 연령: %d세\n" +
                "- 성별: %s\n" +
                "- 신장: %s\n" +
                "- 몸무게: %s\n" +
                "- 건강 목표: %s\n" +
                "- 건강 상태: %s\n" +
                "- 생활 습관: %s\n\n" +
                "게시된 상품 목록:\n%s\n\n" +
                "예시 출력:\n" +
                "{\"recommendedProducts\": [\"오메가3\", \"비타민B군\", \"멜라토닌\"], " +
                "\"recommendationReason\": \"사용자의 건강 정보와 게시된 상품들을 종합적으로 고려할 때, 이 제품들이 혈액순환 개선 및 면역력 강화에 효과적입니다.\"}",
            survey.getAge(),
            survey.getGender(),
            survey.getHeight(),
            survey.getWeight(),
            survey.getHealthGoals(),
            survey.getHealthCondition(),
            survey.getLifestyle(),
            dtosJson
        );
    }


    /**
     * 추천 상품 키워드 생성
     * <p>Json 매핑 오류 혹은 API 호출 실패 시 500 에러 반환</p>
     *
     * @param prompt 요청 프롬프트
     * @return 추천 상품 키워드
     */
    private RecommendationResultWrapper createRecommendationResult(String prompt) {
        UserMessage userMessage = new UserMessage(prompt);
        try {
            ChatResponse response = chatModel.call(new Prompt(
                userMessage,
                OpenAiChatOptions.builder().model(ChatModel.GPT_4_O.getValue()).build()
            ));

            String result = response.getResult().getOutput().getText().trim();
            log.info("result : {}", result);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(result, RecommendationResultWrapper.class);
        } catch (Exception e) {
            log.error("추천 상품 결과 생성에 실패했습니다.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "추천 상품 결과 생성에 실패했습니다.");
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
