package com.example.pillyohae.global.message_queue;

import com.example.pillyohae.global.message_queue.handler.CouponMessageHandler;
import com.example.pillyohae.global.message_queue.handler.PaymentMessageHandler;
import com.example.pillyohae.global.message_queue.message.CouponMessage;
import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisMessageSubscriber implements MessageListener {

    public static List<String> messageList = new ArrayList<String>();
    private final ObjectMapper objectMapper;
    private final CouponMessageHandler couponMessageHandler;
    private final PaymentMessageHandler paymentMessageHandler;

    public RedisMessageSubscriber(ObjectMapper objectMapper, CouponMessageHandler couponMessageHandler, PaymentMessageHandler paymentMessageHandler) {
        this.objectMapper = objectMapper;
        this.couponMessageHandler = couponMessageHandler;
        this.paymentMessageHandler = paymentMessageHandler;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = message.toString();
        messageBody = messageBody.substring(1, messageBody.length() - 1);
        messageBody = StringEscapeUtils.unescapeJson(messageBody);
        try {
            JSONObject jsonObject = objectMapper.readValue(messageBody, JSONObject.class);
            if("coupon".equals(jsonObject.get("domainType"))){
                CouponMessage couponMessage = objectMapper.readValue(messageBody,CouponMessage.class);
                couponMessageHandler.handle(couponMessage);
            } else if ("payment".equals(jsonObject.get("domainType"))) {
                PaymentMessage paymentMessage = objectMapper.readValue(messageBody,PaymentMessage.class);
                paymentMessageHandler.handle(paymentMessage);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

}