package com.example.pillyohae.coupon.repository;

import com.example.pillyohae.coupon.dto.FindCouponListResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;

import java.util.List;

public interface CouponTemplateQueryRepository {
    List<FindCouponListResponseDto.CouponInfo> findCouponList(CouponTemplate.CouponStatus status);
}
