package com.example.pillyohae.coupon.controller;

import com.example.pillyohae.coupon.dto.CreateCouponTemplateRequestDto;
import com.example.pillyohae.coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.coupon.dto.FindCouponListResponseDto;
import com.example.pillyohae.coupon.dto.GiveCouponResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;


    @PostMapping
    public ResponseEntity<CreateCouponTemplateResponseDto> createCouponTemplate(@RequestBody @Valid CreateCouponTemplateRequestDto createCouponTemplateRequestDto,
                                                                                Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.createCouponTemplate(createCouponTemplateRequestDto));
    }


    @PostMapping("/{couponTemplateId}/issue")
    public ResponseEntity<GiveCouponResponseDto> giveCoupon(
            @PathVariable(name = "couponTemplateId") Long couponTemplateId, Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.giveCoupon(authentication.getName(), couponTemplateId));
    }

    // 공개된 쿠폰 조회
    @GetMapping
    public ResponseEntity<FindCouponListResponseDto> getAvailableCoupons(Authentication authentication,
                                                                         @RequestParam(required = false)CouponTemplate.CouponStatus couponStatus) {
        return ResponseEntity.ok(couponService.findCouponList(couponStatus));
    }


}
