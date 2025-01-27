package com.example.pillyohae.coupon.dto;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CouponUpateStatusResponseDto {
    private final CouponTemplate.CouponStatus status;
    private final UUID id;
}
