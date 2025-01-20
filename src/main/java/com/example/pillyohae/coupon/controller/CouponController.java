package com.example.pillyohae.coupon.controller;

import com.example.pillyohae.coupon.dto.CouponGiveResponseDto;
import com.example.pillyohae.coupon.dto.CouponTemplateCreateRequestDto;
import com.example.pillyohae.coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.coupon.dto.CouponListResponseDto;
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
    @GetMapping
    public ResponseEntity<CouponListResponseDto> getAvailableCoupons(Authentication authentication,
                                                                     @RequestParam(required = false)CouponTemplate.CouponStatus couponStatus) {
        return ResponseEntity.ok(couponService.findCouponList(couponStatus));
    }


}
