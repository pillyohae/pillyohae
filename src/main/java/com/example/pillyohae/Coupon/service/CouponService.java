package com.example.pillyohae.Coupon.service;

import com.example.pillyohae.Coupon.entity.CouponTemplate;
import com.example.pillyohae.Coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.Coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    private final CouponTemplateRepository couponTemplateRepository;
    private final IssuedCouponRepository issuedCouponRepository;


    @Transactional
    public void expireCoupon() {
        expireCouponTemplate();
        expireIssuedCoupon();
    }

    @Transactional
    protected void expireCouponTemplate() {
        int maxAttempts = 10; // 최대 시도 횟수
        int attempt = 0;
        int batchSize = 1000; // 배치 사이즈
        boolean hasExpiredCoupons = true;
        LocalDateTime now = LocalDateTime.now();
        // 최대 10000개를 처리함
        while(hasExpiredCoupons && attempt < maxAttempts) {
            int updatedCount = issuedCouponRepository.updateStatusByExpiredAtWithLimit(
                    now,
                    CouponTemplate.CouponStatus.ACTIVE.toString(),
                    batchSize
            );

            List<CouponTemplate> remainingExpiredCoupons =
                    couponTemplateRepository.findAllByExpiredAtBefore(now);

            hasExpiredCoupons = !remainingExpiredCoupons.isEmpty();
            attempt++;

            if(hasExpiredCoupons) {
                alertRemainCoupon(remainingExpiredCoupons, attempt, updatedCount);
            }
        }
        if(hasExpiredCoupons) {
            failExpireAllCoupon(maxAttempts);
            // 알림 발송 또는 다른 처리
        }
    }

    @Transactional
    protected void expireIssuedCoupon() {
        int maxAttempts = 3; // 최대 시도 횟수
        int attempt = 0;
        int batchSize = 100; // 배치 사이즈
        boolean hasExpiredCoupons = true;
        LocalDateTime now = LocalDateTime.now();
        // 최대 300개를 처리함
        while(hasExpiredCoupons && attempt < maxAttempts) {
            int updatedCount = couponTemplateRepository.updateStatusByExpiredAtWithLimit(
                    now,
                    CouponTemplate.CouponStatus.ACTIVE.toString(),
                    batchSize
            );

            List<CouponTemplate> remainingExpiredCoupons =
                    couponTemplateRepository.findAllByExpiredAtBefore(now);

            hasExpiredCoupons = !remainingExpiredCoupons.isEmpty();
            attempt++;

            if(hasExpiredCoupons) {
                alertRemainCoupon(remainingExpiredCoupons, attempt, updatedCount);
            }
        }
        if(hasExpiredCoupons) {
            failExpireAllCoupon(maxAttempts);
            // 알림 발송 또는 다른 처리
        }
    }

    private static void alertRemainCoupon(List<CouponTemplate> remainingExpiredCoupons, int attempt, int updatedCount) {
        log.warn("Still have {} expired coupons after attempt {}. Updated {} coupons.",
                remainingExpiredCoupons.size(), attempt, updatedCount);
    }

    private static void failExpireAllCoupon(int maxAttempts) {
        log.error("Failed to expire all coupons after {} attempts", maxAttempts);
    }




}
