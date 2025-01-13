package com.example.pillyohae.coupon.controller;

import com.example.pillyohae.coupon.dto.CreateCouponTemplateRequestDto;
import com.example.pillyohae.coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.coupon.dto.FindCouponListToUseResponseDto;
import com.example.pillyohae.coupon.dto.GiveCouponResponseDto;
import com.example.pillyohae.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;


    @PostMapping("/create")
    public ResponseEntity<CreateCouponTemplateResponseDto> createCouponTemplate(@RequestBody @Valid CreateCouponTemplateRequestDto createCouponTemplateRequestDto,
                                                                                Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.createCouponTemplate(authentication.getName(), createCouponTemplateRequestDto));
    }


    @PostMapping("/{couponTemplateId}/issue")
    public ResponseEntity<GiveCouponResponseDto> giveCoupon(
            @PathVariable(name = "couponTemplateId") Long couponTemplateId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.giveCoupon(authentication.getName(), couponTemplateId));
    }

    // order를 보고 맞는 조건의 쿠폰들을 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<FindCouponListToUseResponseDto> getCouponListToUse( Authentication authentication, @PathVariable(name = "orderId") UUID orderId) {
        return ResponseEntity.ok(couponService.findCouponListToUse(authentication.getName(), orderId));
    }
}
