package com.example.main.coupon.dto;

import com.example.common.coupon.entity.CouponTemplate;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CouponListResponseDto {

    private List<CouponInfo> couponList;

    public CouponListResponseDto(List<CouponInfo> couponList) {
        this.couponList = couponList;
    }

    @Getter
    @NoArgsConstructor
    public static class CouponInfo {

        private UUID couponId;
        private String couponName;
        private String couponDescription;
        private CouponTemplate.DiscountType discountType;
        private Long fixedAmount;
        private Long fixedRate;
        private Long maxDiscountAmount;
        private Long minimumPrice;
        private LocalDateTime expiredAt;

        @QueryProjection
        public CouponInfo(UUID couponId, String couponName, String couponDescription,
            CouponTemplate.DiscountType discountType, Long fixedAmount, Long fixedRate,
            Long maxDiscountAmount, Long minimumPrice, LocalDateTime expiredAt) {
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
}
