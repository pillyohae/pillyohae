package com.example.pillyohae.global.message_queue.handler;

import com.example.pillyohae.global.message_queue.message.DomainMessage;

// 도메인별 메세지 처리기 인터페이스
public interface DomainMessageHandler<T extends DomainMessage> {
    void handle(Object message);
    Class<T> supportedType();
}
