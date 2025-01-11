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

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<CreateCouponTemplateResponseDto> createCouponTemplate(@RequestBody @Valid CreateCouponTemplateRequestDto createCouponTemplateRequestDto,
                                                                               Authentication authentication) {
        return ResponseEntity.ok(couponService.createCouponTemplate( authentication.getName(), createCouponTemplateRequestDto));
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/{couponTemplateId}/issue")
    public ResponseEntity<GiveCouponResponseDto> giveCoupon(Authentication authentication, @PathVariable(name = "couponTemplateId") Long couponTemplateId) {
        return ResponseEntity.ok(couponService.giveCoupon(authentication.getName(), couponTemplateId));
    }

}
