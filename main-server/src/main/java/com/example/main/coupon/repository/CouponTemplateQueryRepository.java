package com.example.main.coupon.repository;

import com.example.common.coupon.entity.CouponTemplate;
import com.example.main.coupon.dto.CouponTemplateListResponseDto;

import java.util.List;

public interface CouponTemplateQueryRepository {
    List<CouponTemplateListResponseDto.CouponInfo> findCouponList(CouponTemplate.CouponStatus status);
}
