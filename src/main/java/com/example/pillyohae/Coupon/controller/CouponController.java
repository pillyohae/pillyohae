package com.example.pillyohae.Coupon.controller;

import com.example.pillyohae.Coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
}
