package com.example.pillyohae.Coupon.service;

import com.example.pillyohae.Coupon.dto.CreateCouponTemplateRequestDto;
import com.example.pillyohae.Coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.Coupon.entity.CouponTemplate;
import com.example.pillyohae.Coupon.entity.IssuedCoupon;
import com.example.pillyohae.Coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.Coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    private final CouponTemplateRepository couponTemplateRepository;
    private final IssuedCouponRepository issuedCouponRepository;



    @Transactional
    public void expireCoupon() {
        List<Long> updatedCouponTemplateIds =  expireCouponTemplate();
        expireIssuedCoupon(updatedCouponTemplateIds);
    }

    @Transactional
    protected void expireIssuedCoupon(List<Long> updatedCouponTemplateIds) {
        int maxAttempts = 10; // 최대 시도 횟수
        int attempt = 0;
        int batchSize = 1000; // 배치 사이즈
        boolean hasExpiredCoupons = true;
        LocalDateTime now = LocalDateTime.now();
        // 최대 10000개를 처리함
        // template마다 실행
        for(Long updatedCouponTemplateId : updatedCouponTemplateIds) {
            while(hasExpiredCoupons && attempt < maxAttempts) {
                // couponTemplate을 통해 만료시킴
                int updatedCount = issuedCouponRepository.updateIssuedCouponStatusByCouponTemplate_Id(
                        updatedCouponTemplateId,
                        IssuedCoupon.CouponStatus.EXPIRED.toString(),
                        IssuedCoupon.CouponStatus.AVAILABLE.toString(),
                        batchSize
                );

                int remainCouponCount = issuedCouponRepository.countIssuedCouponByCouponTemplate_Id(updatedCouponTemplateId);

                hasExpiredCoupons = remainCouponCount != 0;
                attempt++;

                if(hasExpiredCoupons) {
                    alertRemainCoupon(remainCouponCount, attempt, updatedCount);
                }
            }
            if(hasExpiredCoupons) {
                failExpireAllCoupon(maxAttempts);
                // 알림 발송 또는 다른 처리
            }
        }

    }

    @Transactional
    protected List<Long> expireCouponTemplate() {
        int maxAttempts = 3; // 최대 시도 횟수
        int attempt = 0;
        int batchSize = 100; // 배치 사이즈
        boolean hasExpiredCoupons = true;
        LocalDateTime now = LocalDateTime.now();

        List<Long> updatedCouponAllIds = new ArrayList<>();

        // 최대 300개를 처리함
        // 최대 3회 시도 한번에 100개 처리
        while(hasExpiredCoupons && attempt < maxAttempts) {
            // 만료시킬 쿠폰의 식별자를 batch 크기만큼 가져옴
            List<Long> updatedCouponIds = couponTemplateRepository.findExpiredTemplateIds(
                    now,
                    CouponTemplate.CouponStatus.INACTIVE.toString(),
                    batchSize
            );
            // 식별자 리스트를 통해 모두 만료시킴
            int updatedCount = couponTemplateRepository.updateTemplateStatus(
                    updatedCouponIds,
                    CouponTemplate.CouponStatus.INACTIVE.toString()
            );
            updatedCouponAllIds.addAll(updatedCouponIds);

            // 만료되지 않은 쿠폰을 확인
            int remainCouponCount = couponTemplateRepository.countByExpiredAt(now);

            hasExpiredCoupons = remainCouponCount != 0;
            attempt++;

            if(hasExpiredCoupons) {
                alertRemainCoupon(remainCouponCount, attempt, updatedCount);
            }
        }
        if(hasExpiredCoupons) {
            failExpireAllCoupon(maxAttempts);
            // 알림 발송 또는 다른 처리
        }

        return updatedCouponAllIds;
    }

    private static void alertRemainCoupon(int count, int attempt, int updatedCount) {
        log.warn("Still have {} expired coupons after attempt {}. Updated {} coupons.",
                count, attempt, updatedCount);
    }

    private static void failExpireAllCoupon(int maxAttempts) {
        log.error("Failed to expire all coupons after {} attempts", maxAttempts);
    }




}
