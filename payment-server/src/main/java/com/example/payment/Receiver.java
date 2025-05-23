package com.example.payment;

import com.example.payment.message.PaymentMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Receiver {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "order1")
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        try {
            JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);
            if ("payment".equals(jsonObject.get("domainType"))) {
                PaymentMessage paymentMessage = objectMapper.readValue(message,PaymentMessage.class);
                messageService.requestPayment(paymentMessage.getTossRequest());
            }
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        } catch (ParseException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}