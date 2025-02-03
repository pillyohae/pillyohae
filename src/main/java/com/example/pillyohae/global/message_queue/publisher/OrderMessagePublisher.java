package com.example.pillyohae.global.message_queue.publisher;

import com.example.pillyohae.global.message_queue.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMessagePublisher implements MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void directSendMessage(Message messageDto) {
        // 2. Direct Exchange를 이용하여 라우팅 키(order.pizza)를 기반으로 queue1로 데이터를 전송합니다.
        rabbitTemplate.convertAndSend("exchange.direct","order" , messageDto);
    }
}