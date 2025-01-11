package com.example.pillyohae.Coupon.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GiveCouponResponseDto {
    private Long id;

    public GiveCouponResponseDto(Long id) {
        this.id = id;
    }
}
