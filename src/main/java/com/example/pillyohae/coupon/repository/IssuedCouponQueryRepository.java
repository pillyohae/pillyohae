package com.example.pillyohae.coupon.repository;

import com.example.pillyohae.coupon.dto.FindCouponListToUseResponseDto;

import java.util.List;

public interface IssuedCouponQueryRepository {
    // 유저가 가진 쿠폰중에 최소사용금액이 결제 총액보다 낮은경우를 찾음
    List<FindCouponListToUseResponseDto.CouponInfo> findCouponListToUse(Long price, Long userId);
}
