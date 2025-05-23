package com.example.main.global.message_queue.message;

/**
 * 메시지 객체를 위한 인터페이스 도메인 타입을 반환하는 공통 메서드 정의
 */
public interface Message {

    /**
     * 메시지의 도메인 타입 반환
     *
     * @return 도메인 타입 (ex: "payment", "order" 등)
     */
    String getDomainType();
}
