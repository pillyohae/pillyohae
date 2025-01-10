package com.example.pillyohae.global.scheduler;

import com.example.pillyohae.Coupon.entity.CouponTemplate;
import com.example.pillyohae.Coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.Coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponScheduler {
    private final CouponService couponService;
    @Scheduled(cron = "0 0 2 * * ?") // 매일 오전 2시
    public void deleteExpiredCoupons() {
        couponService.expireCoupon();

    }
}
