package com.example.pillyohae.coupon.dto;

import lombok.Getter;

@Getter
public class CreateCouponTemplateResponseDto {
    private Long id;

    public CreateCouponTemplateResponseDto(Long id) {
        this.id = id;
    }

    public CreateCouponTemplateResponseDto() {}
}
