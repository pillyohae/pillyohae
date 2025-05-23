package com.example.main.coupon.dto;

import com.example.common.coupon.entity.CouponTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CouponUpdateStatusResponseDto {

    private final CouponTemplate.CouponStatus status;
    private final UUID id;
}
