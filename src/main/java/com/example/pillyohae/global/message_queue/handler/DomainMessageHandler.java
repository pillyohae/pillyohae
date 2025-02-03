package com.example.pillyohae.global.message_queue.handler;

/**
 * 도메인별 메시지 처리기 인터페이스
 *
 * @param <T> 메시지 유형
 */
public interface DomainMessageHandler<T> {

    /**
     * 메시지를 처리하는 메서드
     *
     * @param message 처리할 메시지 객체
     */
    void handle(Object message);
}
