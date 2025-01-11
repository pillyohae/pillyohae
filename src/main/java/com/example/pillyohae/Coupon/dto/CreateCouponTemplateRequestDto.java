package com.example.pillyohae.Coupon.dto;

import com.example.pillyohae.Coupon.entity.CouponTemplate;
import com.example.pillyohae.global.validation.ValidCouponPeriod;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@ValidCouponPeriod
public class CreateCouponTemplateRequestDto {
    @NotNull
    @Length(min = 1, max = 50)
    private String couponName;
    @NotNull
    @Length(min = 1, max = 100)
    private String couponDescription;
    @NotNull
    private CouponTemplate.DiscountType discountType;
    @PositiveOrZero
    @NotNull
    private Double fixedAmount;
    @PositiveOrZero
    @NotNull
    private Double fixedRate;
    @NotNull
    private Double maxDiscountAmount;

    @NotNull
    @Positive
    private Integer maxIssueCount;

    @NotNull
    @FutureOrPresent  // @AfterNow 대신 표준 어노테이션 사용
    private LocalDateTime startAt;

    @NotNull
    @Future          // 만료 시간은 반드시 미래여야 함
    private LocalDateTime expireAt;



    public CreateCouponTemplateRequestDto(String couponName, String couponDescription, CouponTemplate.DiscountType discountType, Double fixedAmount, Double fixedRate, Double maxDiscountAmount, LocalDateTime startAt, LocalDateTime expireAt, Integer maxIssueCount) {
        this.couponName = couponName;
        this.couponDescription = couponDescription;
        this.discountType = discountType;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.startAt = startAt;
        this.expireAt = expireAt;
        this.maxIssueCount = maxIssueCount;
    }
}
