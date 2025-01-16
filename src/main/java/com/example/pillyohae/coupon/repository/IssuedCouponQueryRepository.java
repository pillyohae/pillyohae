package com.example.pillyohae.coupon.repository;

import com.example.pillyohae.coupon.dto.FindCouponListResponseDto;

import java.util.List;

public interface IssuedCouponQueryRepository {
    // 유저가 가진 쿠폰중에 최소사용금액이 결제 총액보다 낮은경우를 찾음
    List<FindCouponListResponseDto.CouponInfo> findCouponListByPriceAndUserId(Long price, Long userId);

}
