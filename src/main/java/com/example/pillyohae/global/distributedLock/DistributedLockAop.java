package com.example.pillyohae.global.distributedLock;

import static com.example.pillyohae.global.distributedLock.KeyValueProcessor.kv;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @DistributedLock 어노테이션이 적용된 메서드에 대해 Redisson 기반의 분산 락을 적용하는 AOP 클래스.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    /**
     * 분산 락이 적용된 메서드 실행 시 락을 획득하고, 메서드 실행 후 락을 해제하는 메서드.
     *
     * @param joinPoint AOP의 실행 지점을 나타내는 객체
     * @return 메서드 실행 결과
     * @throws Throwable 예외 발생 시 예외를 던짐
     */
    @Around("@annotation(com.example.pillyohae.global.distributedLock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 락의 고유 키를 생성 (ex: "LOCK:orderId_1234")
        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(
            signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            // 락 획득 시도 (대기 시간 및 임대 시간 적용)
            boolean available = rLock.tryLock(distributedLock.waitTime(),
                distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                return false; // 락 획득 실패 시 false 반환
            }

            // 락을 획득한 상태에서 메서드 실행
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException(); // 쓰레드 인터럽트 예외 발생 시 처리
        } finally {
            try {
                // 락 해제
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already Unlocked {} {}",
                    kv("serviceName", method.getName()),
                    kv("key", key)
                );
            }
        }
    }
}
