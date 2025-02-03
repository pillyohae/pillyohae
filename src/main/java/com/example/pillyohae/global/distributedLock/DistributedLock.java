package com.example.pillyohae.global.distributedLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


/**
 * Redisson 을 활용한 분산 락을 적용하는 어노테이션. 특정 메서드에 락을 설정하여 동시성 문제를 방지하는 역할.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 락의 고유 키 (ex: "orderId")
     */
    String key();

    /**
     * 락을 설정할 시간 단위 (기본값: 초 단위)
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락을 기다릴 시간 (기본값: 5초)
     */
    long waitTime() default 5L;

    /**
     * 락 임대 시간 (기본값: 3초) 해당 시간이 지나면 락이 자동으로 해제됨.
     */
    long leaseTime() default 3L;
}
