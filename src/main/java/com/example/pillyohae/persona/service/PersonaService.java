package com.example.pillyohae.persona.service;

import com.example.pillyohae.persona.dto.PersonaMessageCreateResponseDto;
import com.example.pillyohae.persona.dto.PersonaMessageDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonaService {

    private final OpenAiImageModel openAiImageModel;
    private final OpenAiChatModel chatModel;

    public Image generatePersonaFromProduct(String productImageUrl) {
        // 1. 상품 이미지 분석
        String productDescription = analyzeProductImage(productImageUrl);
        log.info("productDescription : {}, productImageUrl: {}", productDescription, productImageUrl);

        // 2. 페르소나 프롬프트 생성
        String prompt = createImagePrompt(productDescription);
        log.info("persona prompt : {}, productImageUrl: {}", prompt, productImageUrl);

        // 3. 이미지 생성 요청
        return generatePersonaImages(prompt);
    }

    private String analyzeProductImage(String imageUrl) {
        // Vision API를 사용하여 상품 이미지 분석
        UserMessage userMessage;
        try {
            userMessage = new UserMessage("Please explain the main characteristics by analyzing only the product excluding the background from the product image.",
                new Media(MimeTypeUtils.IMAGE_PNG, new URL(imageUrl)));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        ChatResponse response = chatModel.call(new Prompt(userMessage,
            OpenAiChatOptions.builder().model(OpenAiApi.ChatModel.GPT_4_O.getValue()).build()));

        return response.getResult().getOutput().getContent();
    }

    private String createImagePrompt(String productDescription) {
        // 비용 절감을 위한 효율적인 프롬프트 생성
        return String.format(
            "Create a cute cartoon character persona based on this product: %s. " +
                "Style: anime-inspired, friendly mascot. " +
                "Make it simple but appealing, suitable for marketing.",
            productDescription
        );
    }

    private Image generatePersonaImages(String prompt) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .quality("hd")
            .N(1)
            .height(1024)
            .width(1024).build();

        ImageResponse response = openAiImageModel.call(
            new ImagePrompt(prompt, options)
        );

        return response.getResults().get(0).getOutput();
    }

    /**
     * 상품의 페르소나 메시지 생성
     *
     * @param productIngredient 상품 성분
     * @return 페르소나 메시지 리스트
     */
    public List<PersonaMessageCreateResponseDto> createPersonaMessageFromProduct(String productIngredient) {

        // 1. 프롬프트 생성
        String prompt = createPersonaMessagePrompt(productIngredient);

        // 2. 페르소나 메시지 생성
        PersonaMessageDto[] result = generatePersonaMessages(prompt);

        // 3. DTO 변환
        return Arrays.stream(result).map((r) -> new PersonaMessageCreateResponseDto(r.getMessage())).toList();
    }

    /**
     * 프롬프트를 이용하여 페르소나 메시지 생성
     *
     * @param prompt 상품 성분
     * @return 페르소나 메시지 리스트
     */
    public PersonaMessageDto[] generatePersonaMessages(String prompt) {
        try {
            UserMessage userMessage = new UserMessage(prompt);

            ChatResponse response = chatModel.call(new Prompt(userMessage,
                OpenAiChatOptions.builder().model(OpenAiApi.ChatModel.GPT_4_O.getValue()).build()));

            String result = response.getResult().getOutput().getContent();
            log.info("persona message result : {}", result);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(result, new TypeReference<>() {
            });

        } catch (Exception e) {
            log.error("페르소나 메시지 생성에 실패했습니다.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "페르소나 메시지 생성에 실패했습니다.");
        }
    }

    /**
     * 메세지 생성 프롬프트
     *
     * @param productIngredient 상품 성분
     * @return 페르소나 메시지 생성 프롬프트
     */
    private String createPersonaMessagePrompt(String productIngredient) {
        return String.format(
            "영양제의 특징을 살려 가상의 성격을 만들고 자신을 돋보이게 하는 대사를 한 문장씩 3개 만들어주세요. "
                + "말투는 귀엽게 표현하고 이모지를 붙여주세요. 응답은 항상\"message\" 키를 포함하는 JSON 객체의 배열이어야 합니다. "
                + "또한 응답에는 코드블럭, 개행을 제거해주세요. "
                + "응답 형태: {\"message\":대사1},{\"message\":대사2},{\"message\":대사3} "
                + "영양제: " + productIngredient
        );
    }
}
