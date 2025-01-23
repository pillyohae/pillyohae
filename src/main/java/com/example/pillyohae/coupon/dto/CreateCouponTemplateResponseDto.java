package com.example.pillyohae.coupon.dto;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
public class CreateCouponTemplateResponseDto {
    private String couponName;
    private String couponDescription;
    private CouponTemplate.DiscountType discountType;
    private CouponTemplate.ExpiredType expiredType;
    private Long fixedAmount;
    private Long fixedRate;
    private Long maxDiscountAmount;
    private Long minimumPrice;
    private Integer maxIssueCount;
    private LocalDateTime startAt;
    private LocalDateTime expiredAt;
    private Integer couponLifetime;

    public CreateCouponTemplateResponseDto(String couponName, String couponDescription, CouponTemplate.DiscountType discountType, CouponTemplate.ExpiredType expiredType, Long fixedAmount, Long fixedRate, Long maxDiscountAmount, Long minimumPrice, Integer maxIssueCount, LocalDateTime startAt, LocalDateTime expiredAt, Integer couponLifetime) {
        this.couponName = couponName;
        this.couponDescription = couponDescription;
        this.discountType = discountType;
        this.expiredType = expiredType;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minimumPrice = minimumPrice;
        this.maxIssueCount = maxIssueCount;
        this.startAt = startAt;
        this.expiredAt = expiredAt;
        this.couponLifetime = couponLifetime;
    }

    public CreateCouponTemplateResponseDto() {}
}
