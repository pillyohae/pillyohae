package com.example.main.coupon.controller;

import com.example.common.coupon.entity.CouponTemplate;
import com.example.main.coupon.dto.CouponGiveResponseDto;
import com.example.main.coupon.dto.CouponTemplateCreateRequestDto;
import com.example.main.coupon.dto.CouponTemplateListResponseDto;
import com.example.main.coupon.dto.CreateCouponTemplateResponseDto;
import com.example.main.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * CouponController
 * <p>
 * 쿠폰 관련 REST API를 제공하는 컨트롤러
 * - 쿠폰 템플릿 생성
 * - 쿠폰 발급
 * - 사용 가능한 쿠폰 조회
 */
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * 쿠폰 템플릿 생성 API
     *
     * @param createCouponTemplateRequestDto 쿠폰 생성 요청 DTO
     * @return 생성된 쿠폰 템플릿 정보 응답 DTO
     */
    @PostMapping
    public ResponseEntity<CreateCouponTemplateResponseDto> createCouponTemplate(
        @RequestBody @Valid CouponTemplateCreateRequestDto createCouponTemplateRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(couponService.createCouponTemplate(createCouponTemplateRequestDto));
    }

    /**
     * 특정 쿠폰을 사용자에게 발급하는 API
     *
     * @param couponTemplateId 쿠폰 템플릿 ID
     * @param authentication   현재 로그인한 사용자 정보
     * @return 발급된 쿠폰 정보 응답 DTO
     */
    @PostMapping("/{couponTemplateId}/issue")
    public ResponseEntity<CouponGiveResponseDto> giveCoupon(
        @PathVariable(name = "couponTemplateId") UUID couponTemplateId,
        Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(couponService.giveCoupon(authentication.getName(), couponTemplateId));
    }

    /**
     * 사용 가능한 쿠폰 목록 조회 API
     *
     * @return 현재 활성 상태의 쿠폰 목록 응답 DTO
     */
    @GetMapping("/available")
    public ResponseEntity<CouponTemplateListResponseDto> getAvailableCoupons() {
        return ResponseEntity.ok(couponService.findCouponList(CouponTemplate.CouponStatus.ACTIVE));
    }
}

