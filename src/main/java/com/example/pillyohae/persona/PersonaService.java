package com.example.pillyohae.persona;

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
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.net.MalformedURLException;
import java.net.URL;

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
        String prompt = createPersonaPrompt(productDescription);
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

    private String createPersonaPrompt(String productDescription) {
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
}
