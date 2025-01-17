package com.example.pillyohae.coupon.dto;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponGiveResponseDto {
    private Long couponId;
    private String couponName;
    private String couponDescription;
    private CouponTemplate.DiscountType discountType;
    private Long fixedAmount;
    private Long fixedRate;
    private Long maxDiscountAmount;
    private Long minimumPrice;
    private LocalDateTime expiredAt;

    public CouponGiveResponseDto(Long couponId, String couponName, String couponDescription, CouponTemplate.DiscountType discountType, Long fixedAmount, Long fixedRate, Long maxDiscountAmount, Long minimumPrice, LocalDateTime expiredAt) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.couponDescription = couponDescription;
        this.discountType = discountType;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minimumPrice = minimumPrice;
        this.expiredAt = expiredAt;
    }

}
