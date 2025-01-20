package com.example.pillyohae.global.message_queue;

import com.example.pillyohae.global.message_queue.message.CouponMessage;
import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublisher {
    private final RedissonClient redisson;
    private static final String PAYMENT_TOPIC = "queue:payment";
    private static final String COUPON_TOPIC = "queue:coupon";
    private final DomainMessageConsumer domainMessageConsumer;
    private final ObjectMapper objectMapper;

    public void publishPaymentEvent(PaymentMessage paymentMessage) {
        try {
            RQueue<String> queue = redisson.getQueue(PAYMENT_TOPIC);
            String jsonMessage = objectMapper.writeValueAsString(paymentMessage);  // JSON으로 직렬화
            queue.add(jsonMessage);
            log.info("Published payment message: {}", jsonMessage);  // 로깅 추가
        } catch (JsonProcessingException e) {
            log.error("Failed to publish payment message", e);
        }
    }

    public void publishCouponEvent(CouponMessage couponMessage) {
        try {
            RQueue<String> queue = redisson.getQueue(COUPON_TOPIC);
            String jsonMessage = objectMapper.writeValueAsString(couponMessage);  // JSON으로 직렬화

            queue.add(jsonMessage);
            log.info("Published payment message: {}", jsonMessage);  // 로깅 추가
        } catch (JsonProcessingException e) {
            log.error("Failed to publish payment message", e);
        }
    }

    // publisher가 생성될때 컨슈밍 시작
    @PostConstruct
    public void init() {
        // 큐 생성
        RQueue<String> paymentQueue = redisson.getQueue(PAYMENT_TOPIC);
        RQueue<String> couponQueue = redisson.getQueue(COUPON_TOPIC);

        // 큐가 제대로 생성되었는지 로그
        log.info("Payment queue initialized: {}", paymentQueue.getName());
        log.info("Coupon queue initialized: {}", couponQueue.getName());
        domainMessageConsumer.startConsuming("payment");
        domainMessageConsumer.startConsuming("coupon");
    }
}