package com.example.pillyohae.coupon.service;

import com.example.pillyohae.coupon.dto.*;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.global.message_queue.publisher.RedisMessagePublisher;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    private final CouponIssueService couponIssueService;

    // message queue
    private final RedisMessagePublisher messagePublisher;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, Integer> intRedisTemplate;

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
                , couponTemplate.getCouponLifetime());
    }

    // 유저 개인이 발행
    @Transactional
    public CouponGiveResponseDto giveCoupon(String email, UUID couponTemplateId) {

        IssuedCoupon issuedCoupon = tryIssueCoupon(couponTemplateId, email);

        CouponTemplate couponTemplate = issuedCoupon.getCouponTemplate();

        return new CouponGiveResponseDto(issuedCoupon.getId(), couponTemplate.getName(), couponTemplate.getDescription()
                , couponTemplate.getDiscountType(), couponTemplate.getFixedAmount(), couponTemplate.getFixedRate()
                , couponTemplate.getMaxDiscountAmount(), couponTemplate.getMinimumPrice(), couponTemplate.getIssuedCouponExpiredAt());

    }

    public IssuedCoupon tryIssueCoupon(UUID couponTemplateId, String email) {
        // 락 생성
        RLock lock = redissonClient.getLock("coupon:" + couponTemplateId);
        boolean isLocked = false;
        int attempts = 0;
        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                isLocked = lock.tryLock(60, 1,TimeUnit.SECONDS);
                if (isLocked) {
                    return couponIssueService.issueCoupon(couponTemplateId, email);
                }
                // 에러 캐치 후 3회 재시도
            } catch (InterruptedException e) {
                System.out.println("충돌 발생");
                attempts++;
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } finally {
                if (lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "쿠폰 발급 timeout");

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

    @Transactional
    public CouponUpateStatusResponseDto updateCouponStatus(UUID couponTemplateId, CouponTemplate.CouponStatus couponStatus) {
        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        couponTemplate.updateStatus(couponStatus);

        return new CouponUpateStatusResponseDto(couponTemplate.getStatus(),couponTemplate.getId());
    }

    @Transactional
    public void deleteCoupon(UUID couponTemplateId) {
        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        couponTemplate.delete();

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

    public CouponTemplateListResponseDto findCouponList(CouponTemplate.CouponStatus status) {
        List<CouponTemplateListResponseDto.CouponInfo> couponTemplates = couponTemplateRepository.findCouponList(status);
        return new CouponTemplateListResponseDto(couponTemplates);
    }

}
