package com.example.pillyohae.global.message_queue;

import com.example.pillyohae.global.message_queue.handler.DomainMessageHandler;
import com.example.pillyohae.global.message_queue.message.CouponMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
/**
 * 도메인별로 별도의 메세지큐를 생성하고 처리합니다.
 */
public class DomainMessageConsumer {
    private final RedissonClient redisson;
    private final Map<String, DomainMessageHandler> handlers;
    private final ObjectMapper objectMapper;
    private volatile boolean isRunning;

    @Autowired
    public DomainMessageConsumer(RedissonClient redisson, List<DomainMessageHandler> handlers, ObjectMapper objectMapper) {
        this.redisson = redisson;
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        h -> h.supportedType().getSimpleName(),
                        h -> h
                ));
        this.objectMapper = objectMapper;
        this.isRunning = false;
    }

    public void startConsuming(String domainType) {
        isRunning = true;
        RBlockingQueue<String> queue = redisson.getBlockingQueue(getQueueName(domainType));

        CompletableFuture.runAsync(() -> {
            while (isRunning) {
                try {
                    String jsonMessage = queue.take();
                    DomainMessageHandler handler = handlers.get(domainType);

                    if (handler != null) {
                        Object message = objectMapper.readValue(jsonMessage, handler.supportedType());
                        handler.handle(message);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 에러 처리 및 로깅
                    log.error("Error processing message", e);
                }
            }
        });
    }

    private String getQueueName(String domainType) {
        return "queue:" + domainType;
    }

    public void stopConsuming() {
        isRunning = false;
    }
}