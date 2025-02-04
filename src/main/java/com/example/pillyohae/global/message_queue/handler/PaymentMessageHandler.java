package com.example.pillyohae.global.message_queue.handler;

import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.example.pillyohae.global.message_queue.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 결제 관련 메시지를 처리하는 핸들러 (Consumer) 클래스 {@link DomainMessageHandler} 를 구현하며, {@link PaymentMessage}
 * 유형의 메시지를 처리함.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentMessageHandler implements DomainMessageHandler<PaymentMessage> {

    // 결제 처리 서비스
    private final MessageService messageService;

    /**
     * 결제 메시지를 받아서 처리
     *
     * @param message 결제 메시지 객체 (JSON 형식)
     */
    @Override
    public void handle(Object message) {
        PaymentMessage paymentMessage = (PaymentMessage) message;
        try {
            // 결제 요청 실행
            messageService.requestPayment(paymentMessage.getTossRequest());
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}
