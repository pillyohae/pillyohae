package com.example.pillyohae.global.message_queue.publisher;

import com.example.pillyohae.global.message_queue.message.Message;

/**
 * 메시지 발행을 위한 인터페이스 Direct Exchange 방식을 이용하여 메시지를 전송하는 메서드 정의.
 */
public interface MessagePublisher {

    /**
     * 메시지를 Direct Exchange 방식으로 전송
     *
     * @param messageDto 전송할 메시지 객체
     */
    void directSendMessage(Message messageDto);
}
