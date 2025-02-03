package com.example.pillyohae.global.distributedLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP에서 트랜잭션을 분리하여 새로운 트랜잭션을 생성하는 클래스. Propagation.REQUIRES_NEW 를 사용하여 기존 트랜잭션과 별도의 트랜잭션을 수행.
 */
@Component
public class AopForTransaction {

    /**
     * 새로운 트랜잭션을 생성하여 메서드를 실행하는 메서드.
     *
     * @param joinPoint AOP의 실행 지점을 나타내는 객체
     * @return 실행 결과 반환
     * @throws Throwable 예외 발생 시 예외를 던짐
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
