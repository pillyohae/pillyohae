package com.example.pillyohae.global.message_queue.publisher;

import com.example.pillyohae.global.message_queue.message.Message;

public interface MessagePublisher {

    void directSendMessage(Message messageDto);      // Direct Exchange 방식 이용
}