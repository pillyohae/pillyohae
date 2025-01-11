package com.example.pillyohae.Coupon.controller;

import com.example.pillyohae.Coupon.dto.CreateCouponTemplateRequestDto;
import com.example.pillyohae.Coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.Coupon.dto.GiveCouponResponseDto;
import com.example.pillyohae.Coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

}
