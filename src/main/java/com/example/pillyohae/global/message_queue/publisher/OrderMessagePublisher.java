package com.example.pillyohae.global.message_queue.publisher;

import com.example.pillyohae.global.message_queue.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ를 이용한 메시지 발행 클래스
 * <p>
 * {@link MessagePublisher} 를 구현하며, Direct Exchange를 통해 메시지를 전송함.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMessagePublisher implements MessagePublisher {

    // RabbitMQ와 통신하는 객체
    private final RabbitTemplate rabbitTemplate;

    /**
     * 결제 메시지를 RabbitMQ의 Direct Exchange를 통해 전송. order라는 라우팅 키를 사용하여 메시지를 전송한다.
     *
     * @param messageDto 전송할 메시지 객체
     */
    @Override
    public void directSendMessage(Message messageDto) {
//        log.info("Direct Exchange 방식으로 메시지 전송: {}", messageDto);
        rabbitTemplate.convertAndSend("exchange.direct", "order", messageDto);
    }
}
