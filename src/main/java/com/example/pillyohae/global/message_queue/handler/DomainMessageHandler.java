package com.example.pillyohae.global.message_queue.handler;

// 도메인별 메세지 처리기 인터페이스
public interface DomainMessageHandler<T> {
    void handle(Object message);
    Class<T> supportedType();
}
