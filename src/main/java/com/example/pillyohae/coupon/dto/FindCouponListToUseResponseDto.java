package com.example.pillyohae.coupon.dto;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class FindCouponListToUseResponseDto {
    private List<CouponInfo> couponList;

    public FindCouponListToUseResponseDto(List<CouponInfo> couponList) {
        this.couponList = couponList;
    }

    @Getter
    @NoArgsConstructor
    public static class CouponInfo{
        private Long couponId;
        private String couponName;
        private String couponDescription;
        private CouponTemplate.DiscountType discountType;
        private Double fixedAmount;
        private Double fixedRate;
        private Double maxDiscountAmount;
        private LocalDateTime expireAt;

        @QueryProjection
        public CouponInfo(Long couponId, String couponName, String couponDescription, CouponTemplate.DiscountType discountType, Double fixedAmount, Double fixedRate, Double maxDiscountAmount, LocalDateTime expireAt) {
            this.couponId = couponId;
            this.couponName = couponName;
            this.couponDescription = couponDescription;
            this.discountType = discountType;
            this.fixedAmount = fixedAmount;
            this.fixedRate = fixedRate;
            this.maxDiscountAmount = maxDiscountAmount;
            this.expireAt = expireAt;
        }
    }
}
