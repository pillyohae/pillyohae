package com.example.pillyohae.global.message_queue;


import com.example.pillyohae.global.message_queue.handler.PaymentMessageHandler;
import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

/**
 * Redis 를 이용한 메시지 구독 서비스
 * <p>
 * 메시지를 수신하고 도메인 타입에 따라 적절한 핸들러로 전달.
 */
@Service
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final PaymentMessageHandler paymentMessageHandler;

    public RedisMessageSubscriber(ObjectMapper objectMapper,
        PaymentMessageHandler paymentMessageHandler) {
        this.objectMapper = objectMapper;
        this.paymentMessageHandler = paymentMessageHandler;
    }

    /**
     * Redis Pub/Sub 메시지 수신 및 처리
     *
     * @param message 수신된 메시지
     * @param pattern 패턴 (사용하지 않음)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = message.toString();
        messageBody = messageBody.substring(1, messageBody.length() - 1);
        messageBody = StringEscapeUtils.unescapeJson(messageBody);
        try {
            JSONObject jsonObject = objectMapper.readValue(messageBody, JSONObject.class);
            if ("payment".equals(jsonObject.get("domainType"))) {
                PaymentMessage paymentMessage = objectMapper.readValue(messageBody,
                    PaymentMessage.class);
                paymentMessageHandler.handle(paymentMessage);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
