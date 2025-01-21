package com.example.pillyohae.coupon.service;

import com.example.pillyohae.coupon.dto.CouponGiveResponseDto;
import com.example.pillyohae.coupon.dto.CouponListResponseDto;
import com.example.pillyohae.coupon.dto.CouponTemplateCreateRequestDto;
import com.example.pillyohae.coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.global.message_queue.message.CouponMessage;
import com.example.pillyohae.global.message_queue.publisher.RedisMessagePublisher;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;

    // message queue
    private final RedisMessagePublisher messagePublisher;
    private final RedisTemplate<String,Object> objectRedisTemplate;
    private final RedisTemplate<String,Integer> intRedisTemplate;

    // Redission
    private final RedissonClient redissonClient;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private final ObjectMapper objectMapper;

    @Transactional
    public CreateCouponTemplateResponseDto createCouponTemplate(CouponTemplateCreateRequestDto requestDto) {

        CouponTemplate couponTemplate = CouponTemplate.builder()
                .name(requestDto.getCouponName())
                .discountType(requestDto.getDiscountType())
                .expiredType(requestDto.getExpiredType())
                .description(requestDto.getCouponDescription())
                .fixedAmount(requestDto.getFixedAmount())
                .fixedRate(requestDto.getFixedRate())
                .maxDiscountAmount(requestDto.getMaxDiscountAmount())
                .startAt(requestDto.getStartAt())
                .expiredAt(requestDto.getExpiredAt())
                .maxIssuanceCount(requestDto.getMaxIssueCount())
                .minimumPrice(validateMinimumPrice(requestDto.getMinimumPrice()))
                .couponLifetime(requestDto.getCouponLifetime())
                .build();

        couponTemplateRepository.save(couponTemplate);

        return new CreateCouponTemplateResponseDto(couponTemplate.getName(), couponTemplate.getDescription()
                , couponTemplate.getDiscountType(), couponTemplate.getExpiredType(), couponTemplate.getFixedAmount()
                , couponTemplate.getFixedRate(), couponTemplate.getMaxDiscountAmount(), couponTemplate.getMinimumPrice()
                , couponTemplate.getMaxIssuanceCount(), couponTemplate.getStartAt(), couponTemplate.getExpiredAt()
                , couponTemplate.getCouponLifetime().getDays());
    }

    // 유저 개인이 발행
    @Transactional(readOnly = true)
    public CouponGiveResponseDto giveCoupon(String email, UUID couponTemplateId) {

        User user = userService.findByEmail(email);
        // issued coupon과 coupon template을 한번에 가져옴

        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        int attempts = 0;
        boolean success = false;

        while (attempts < MAX_RETRY_ATTEMPTS && !success) {
            try {
                CouponMessage couponMessage = tryIssueCoupon(couponTemplate, user);

                return new CouponGiveResponseDto(couponMessage.getIssuedCouponId(), couponTemplate.getName(), couponTemplate.getDescription()
                        , couponTemplate.getDiscountType(), couponTemplate.getFixedAmount(), couponTemplate.getFixedRate()
                        , couponTemplate.getMaxDiscountAmount(), couponTemplate.getMinimumPrice(), couponTemplate.getIssuedCouponExpiredAt());
                // 인터럽트 발생시 최대 3회 재시도
            } catch (InterruptedException e) {
                attempts++;
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"쿠폰 발급 실패");

    }

    private CouponMessage tryIssueCoupon(CouponTemplate couponTemplate, User user) throws InterruptedException {
        if (couponTemplate.getStartAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아직 발급 시작이 안되었습니다");
        }
        IssuedCoupon issuedCoupon;
        List<IssuedCoupon> userIssuedCoupons = issuedCouponRepository.findIssuedCouponsWithTemplateByUserId(
                user.getId());
        // 락 생성
        RLock lock = redissonClient.getLock("coupon:" + couponTemplate.getId());
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {

                checkDuplicateCoupon(userIssuedCoupons, couponTemplate);

                checkCouponQuantity(couponTemplate);

                checkCouponTemplateStatus(couponTemplate);

                incrementCouponCount(couponTemplate.getId());

                UUID issuedCouponId = UUID.randomUUID();

                // 쿠폰 처리 메세지 issuedCoupon id 를 미리 생성한다 UUID 생성 버전은 default 4
                CouponMessage couponMessage = new CouponMessage(couponTemplate.getId(), issuedCouponId, user.getId(), LocalDateTime.now(), "coupon");
                String message = objectMapper.writeValueAsString(couponMessage);
                messagePublisher.publish(message);
                return couponMessage;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "쿠폰 발급 실패");
    }

    // 쿠폰 중복 검사
    private void checkDuplicateCoupon(List<IssuedCoupon> userIssuedCoupons, CouponTemplate couponTemplate) {
        List<CouponTemplate> userCouponTemplates = userIssuedCoupons.stream()
                .map(IssuedCoupon::getCouponTemplate).toList();

        if (userCouponTemplates.contains(couponTemplate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰을 중복해서 가질 수 없습니다");
        }
    }

    private void checkCouponQuantity(CouponTemplate couponTemplate) {
        if (getCouponCount(couponTemplate) >= couponTemplate.getMaxIssuanceCount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰 수량이 소진되었습니다");
        }
    }

    // 쿠폰 상태 검증
    private void checkCouponTemplateStatus(CouponTemplate couponTemplate) {
        if (couponTemplate.getStatus() != CouponTemplate.CouponStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"사용 가능한 쿠폰이 아닙니다. 현재 상태: " + couponTemplate.getStatus());
        }
    }

    private void incrementCouponCount(UUID couponId) {
        String countKey = "coupon:count:" + couponId;
        ValueOperations<String, Integer> ops = intRedisTemplate.opsForValue();
        ops.increment(countKey);
    }

    private Integer getCouponCount(CouponTemplate couponTemplate) {
        String countKey = "coupon:count:" + couponTemplate.getId();

        // 1. 캐시 존재 여부 확인
        Boolean hasKey = objectRedisTemplate.hasKey(countKey);

        if (Boolean.FALSE.equals(hasKey)) {
            // 2. 캐시가 없으면 DB에서 조회하여 초기화
            synchronized (this) {  // double-checking을 위한 동기화
                hasKey = objectRedisTemplate.hasKey(countKey);
                if (Boolean.FALSE.equals(hasKey)) {
                    initializeCouponCount(couponTemplate);
                }
            }
        }

        // 3. 캐시에서 수량 반환
        String count = String.valueOf(intRedisTemplate.opsForValue().get(countKey));
        return count != null ? Integer.parseInt(count) : 0;
    }

    private void initializeCouponCount(CouponTemplate couponTemplate) {
        String countKey = "coupon:count:" + couponTemplate.getId();

        // Redis에 저장하고 만료시간 설정
        ValueOperations<String, Integer> ops = intRedisTemplate.opsForValue();
        ops.set(countKey, couponTemplate.getCurrentIssuanceCount());
        log.info("Coupon count initialized - couponId: {}, count: {}", couponTemplate.getId(), ops.get(countKey));
    }


    @Transactional
    public CouponListResponseDto findCouponListToUse(String email, Long totalPrice) {
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "로그인이 되어야 합니다");
        }
        User user = userService.findByEmail(email);
        return new CouponListResponseDto(
                issuedCouponRepository.findCouponListByPriceAndUserId(totalPrice, user.getId())
        );
    }

    private Long validateMinimumPrice(Long minimumPrice) {
        if (minimumPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 주문 금액은 필수입니다");
        }
        if (minimumPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 주문 금액은 0 이상이어야 합니다");
        }
        return minimumPrice;
    }

    public CouponListResponseDto findCouponList(CouponTemplate.CouponStatus status) {
        List<CouponListResponseDto.CouponInfo> couponTemplates = couponTemplateRepository.findCouponList(status);
        return new CouponListResponseDto(couponTemplates);
    }

}
