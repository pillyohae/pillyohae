package com.example.pillyohae.coupon.dto;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CouponTemplateListResponseDto {
    private List<CouponInfo> couponList;

    public CouponTemplateListResponseDto(List<CouponTemplateListResponseDto.CouponInfo> couponList) {
        this.couponList = couponList;
    }

    @Getter
    @NoArgsConstructor
    public static class CouponInfo{
        private UUID couponId;
        private String couponName;
        private String couponDescription;
        private CouponTemplate.DiscountType discountType;
        private Long fixedAmount;
        private Long fixedRate;
        private Long maxDiscountAmount;
        private Long minimumPrice;
        private CouponTemplate.ExpiredType expiredType;
        private LocalDateTime expiredAt;
        private Integer couponLifetime;
        private CouponTemplate.CouponStatus couponStatus;

        @QueryProjection
        public CouponInfo(UUID couponId, String couponName, String couponDescription, CouponTemplate.DiscountType discountType, Long fixedAmount, Long fixedRate, Long maxDiscountAmount, Long minimumPrice, CouponTemplate.ExpiredType expiredType, LocalDateTime expiredAt, Period couponLifetime, CouponTemplate.CouponStatus couponStatus) {
            this.couponId = couponId;
            this.couponName = couponName;
            this.couponDescription = couponDescription;
            this.discountType = discountType;
            this.fixedAmount = fixedAmount;
            this.fixedRate = fixedRate;
            this.maxDiscountAmount = maxDiscountAmount;
            this.minimumPrice = minimumPrice;
            this.expiredType = expiredType;
            this.expiredAt = expiredAt;
            this.couponLifetime = couponLifetime.getDays();
            this.couponStatus = couponStatus;
        }
    }
}