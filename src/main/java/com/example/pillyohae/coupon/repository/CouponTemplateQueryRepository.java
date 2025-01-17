package com.example.pillyohae.coupon.repository;

import com.example.pillyohae.coupon.dto.CouponListResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;

import java.util.List;

public interface CouponTemplateQueryRepository {
    List<CouponListResponseDto.CouponInfo> findCouponList(CouponTemplate.CouponStatus status);
}
