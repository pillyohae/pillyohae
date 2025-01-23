package com.example.pillyohae.coupon.repository;

import com.example.pillyohae.coupon.dto.CouponListResponseDto;
import com.example.pillyohae.coupon.dto.CouponTemplateListResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;

import java.util.List;

public interface CouponTemplateQueryRepository {
    List<CouponTemplateListResponseDto.CouponInfo> findCouponList(CouponTemplate.CouponStatus status);
}
