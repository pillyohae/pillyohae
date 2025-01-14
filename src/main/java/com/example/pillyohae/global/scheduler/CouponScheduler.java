package com.example.pillyohae.global.scheduler;

import com.example.pillyohae.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponScheduler {

    private final CouponService couponService;


    // 쿠폰 발행
    @Scheduled(cron = "0 0 * * * ?") // 매시 정각
    public void issuePeriodicalCoupons() {
        log.info("Starting hourly coupon issuance job");
        try {
            couponService.issueCoupons();
        } catch (Exception e) {
            log.error("Error during coupon issuance: ", e);
            // 알림 발송 로직 추가
        }
    }
}
