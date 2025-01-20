package com.example.pillyohae.global.message_queue.handler;

import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.example.pillyohae.global.message_queue.service.MessageService;
import com.example.pillyohae.order.service.OrderService;
import com.example.pillyohae.payment.entity.PayMethod;
import com.example.pillyohae.payment.entity.Payment;
import com.example.pillyohae.payment.entity.TossPaymentsVariables;
import com.example.pillyohae.payment.repository.PaymentRepository;
import com.example.pillyohae.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentMessageHandler implements DomainMessageHandler<PaymentMessage> {

    private final MessageService messageService;

    @Override
    public void handle(Object message) {
        // 주문 관련 비즈니스 로직 처리
        PaymentMessage paymentMessage = (PaymentMessage) message;
        try {
            messageService.savePayment(paymentMessage.getJsonObject());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("결제 처리 완료: {}", UUID.fromString(paymentMessage.getJsonObject().get(TossPaymentsVariables.ORDERID.getValue()).toString()));
    }

    @Override
    public Class<PaymentMessage> supportedType() {
        return PaymentMessage.class;
    }

}