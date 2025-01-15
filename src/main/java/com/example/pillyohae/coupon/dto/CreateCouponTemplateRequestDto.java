package com.example.pillyohae.coupon.dto;

import com.example.pillyohae.coupon.entity.CouponTemplate;
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
    @NotNull
    private CouponTemplate.ExpiredType expiredType;
    @PositiveOrZero
    @NotNull
    private Long fixedAmount;
    @PositiveOrZero
    @NotNull
    private Long fixedRate;
    @NotNull
    private Long maxDiscountAmount;

    @NotNull
    @PositiveOrZero
    private Long minimumPrice;

    @NotNull
    @Positive
    private Integer maxIssueCount;

    @NotNull
    @FutureOrPresent  // @AfterNow 대신 표준 어노테이션 사용
    private LocalDateTime startAt;

    @NotNull
    @Future          // 만료 시간은 반드시 미래여야 함
    private LocalDateTime expiredAt;



    public CreateCouponTemplateRequestDto(String couponName, String couponDescription, CouponTemplate.DiscountType discountType, CouponTemplate.ExpiredType expiredType, Long fixedAmount, Long fixedRate, Long maxDiscountAmount, Long minimumPrice, LocalDateTime startAt, LocalDateTime expiredAt, Integer maxIssueCount) {
        this.couponName = couponName;
        this.couponDescription = couponDescription;
        this.discountType = discountType;
        this.expiredType = expiredType;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minimumPrice = minimumPrice;
        this.startAt = startAt;
        this.expiredAt = expiredAt;
        this.maxIssueCount = maxIssueCount;
    }
}
