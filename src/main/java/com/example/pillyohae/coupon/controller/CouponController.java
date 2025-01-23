package com.example.pillyohae.coupon.controller;

import com.example.pillyohae.coupon.dto.*;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;


    @PostMapping
    public ResponseEntity<CreateCouponTemplateResponseDto> createCouponTemplate(@RequestBody @Valid CouponTemplateCreateRequestDto createCouponTemplateRequestDto,
                                                                                Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.createCouponTemplate(createCouponTemplateRequestDto));
    }


    @PostMapping("/{couponTemplateId}/issue")
    public ResponseEntity<CouponGiveResponseDto> giveCoupon(
            @PathVariable(name = "couponTemplateId") UUID couponTemplateId, Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.giveCoupon(authentication.getName(), couponTemplateId));
    }

    // 공개된 쿠폰 조회
    @GetMapping("/available")
    public ResponseEntity<CouponTemplateListResponseDto> getAvailableCoupons(Authentication authentication) {
        return ResponseEntity.ok(couponService.findCouponList(CouponTemplate.CouponStatus.ACTIVE));
    }
    // 상태에 따른 쿠폰 조회 (관리자만 조회 가능)
    @GetMapping()
    public ResponseEntity<CouponTemplateListResponseDto> getAvailableCoupons(Authentication authentication,
                                                                             @RequestParam(required = false)CouponTemplate.CouponStatus couponStatus) {
        return ResponseEntity.ok(couponService.findCouponList(couponStatus));
    }



}
