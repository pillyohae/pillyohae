package com.example.main.global.distributedLock;

import com.example.common.order.entity.Order;
import com.example.common.order.entity.OrderProduct;
import com.example.main.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.simple.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.main.global.distributedLock.KeyValueProcessor.kv;
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    /**
     * 분산 락이 적용된 메서드 실행 시 락을 획득하고, 메서드 실행 후 락을 해제하는 메서드.
     *
     * @param joinPoint AOP의 실행 지점을 나타내는 객체
     * @return 메서드 실행 결과
     * @throws Throwable 예외 발생 시 예외를 던짐
     */
    @Around("@annotation(com.example.main.global.distributedLock.OrderDistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OrderDistributedLock orderDistributedLock = method.getAnnotation(OrderDistributedLock.class);
        Object object = CustomSpringELParser.getDynamicValue(
                signature.getParameterNames(), joinPoint.getArgs(), orderDistributedLock.key());
        JSONObject jsonObject = objectMapper.readValue(object.toString(), JSONObject.class);
        String orderId = jsonObject.get("orderId").toString();
        Order order = orderRepository.findByOrderIdWithOrderProducts(UUID.fromString(orderId))
                .orElseThrow(
                        () -> new RuntimeException("Order not found")
                );
        List<RLock> rLockList = new ArrayList<>();
        // 상품 id 별로 lock을 생성
        for(OrderProduct orderProduct : order.getOrderProducts()) {
            String key = "product:stock:" + orderProduct.getProductId();
            rLockList.add(redissonClient.getLock(key));
        }


        try {
            boolean available;
            // 락 획득 시도 (대기 시간 및 임대 시간 적용)
            for(RLock rLock : rLockList) {
                available = rLock.tryLock(orderDistributedLock.waitTime(),
                        orderDistributedLock.leaseTime(), orderDistributedLock.timeUnit());
                if(!available) {
                    return false;
                }
            }

            // 락을 획득한 상태에서 메서드 실행
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException(); // 쓰레드 인터럽트 예외 발생 시 처리
        } finally {
            try {
                // 락 해제
                for(RLock rLock : rLockList) {
                    rLock.unlock();
                }
            } catch (IllegalMonitorStateException e) {
                for(RLock rLock : rLockList) {
                    log.info("Redisson Lock Already Unlocked {} {}",
                            kv("serviceName", method.getName()),
                            kv("key", rLock.getName())
                    );
                }
            }
        }
    }
}
