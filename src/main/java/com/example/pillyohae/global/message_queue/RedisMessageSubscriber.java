package com.example.pillyohae.global.message_queue;

import com.example.pillyohae.global.message_queue.message.CouponMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisMessageSubscriber implements MessageListener {

    public static List<String> messageList = new ArrayList<String>();
    private final ObjectMapper objectMapper;

    public RedisMessageSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = message.toString();
        messageBody = messageBody.substring(1, messageBody.length() - 1);
        messageBody = StringEscapeUtils.unescapeJson(messageBody);
        try {
            CouponMessage couponMessage = objectMapper.readValue(messageBody,CouponMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        messageList.add(message.toString());
        System.out.println("Message received: " + message.toString());
    }

}