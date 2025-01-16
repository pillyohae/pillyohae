package com.example.pillyohae.coupon.dto;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.global.validation.ValidCouponPeriod;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

@Getter
@ValidCouponPeriod
@NoArgsConstructor
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
    private Long fixedAmount;

    @PositiveOrZero
    @Max(100)
    private Long fixedRate;

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
    // 일 단위
    private Integer couponLifetime;



    @JsonCreator
    public CreateCouponTemplateRequestDto(
            @JsonProperty("couponName") String couponName,
            @JsonProperty("couponDescription") String couponDescription,
            @JsonProperty("discountType") CouponTemplate.DiscountType discountType,
            @JsonProperty("expiredType") CouponTemplate.ExpiredType expiredType,
            @JsonProperty("fixedAmount") Long fixedAmount,
            @JsonProperty("fixedRate") Long fixedRate,
            @JsonProperty("maxDiscountAmount") Long maxDiscountAmount,
            @JsonProperty("minimumPrice") Long minimumPrice,
            @JsonProperty("startAt") LocalDateTime startAt,
            @JsonProperty("expiredAt") LocalDateTime expiredAt,
            @JsonProperty("maxIssueCount") Integer maxIssueCount,
            @JsonProperty("couponLifetime") Integer couponLifetime
    )  {
        validateDiscountTypeFields(discountType, fixedAmount, fixedRate, maxDiscountAmount);
        validateExpiredTypeFields(expiredType, couponLifetime);
        this.couponName = couponName;
        this.couponDescription = couponDescription;
        this.discountType = discountType;
        this.expiredType = expiredType;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.minimumPrice = minimumPrice;
        this.startAt = startAt;
        this.expiredAt = expiredAt;
        this.maxIssueCount = maxIssueCount;
        this.couponLifetime = couponLifetime;

    }

    private void validateDiscountTypeFields(CouponTemplate.DiscountType discountType, Long fixedAmount, Long fixedRate, Long maxDiscountAmount) {

        if (discountType == CouponTemplate.DiscountType.FIXED_AMOUNT){
            validateFixedAmountType(fixedAmount);
            if(maxDiscountAmount == null){
                this.maxDiscountAmount = fixedAmount;
            }
        }

        if (discountType == CouponTemplate.DiscountType.PERCENTAGE){
            validateFixedRateType(fixedRate);
            this.maxDiscountAmount = Objects.requireNonNullElse(maxDiscountAmount, 1000_000_000L);

        }
    }

    private void validateExpiredTypeFields(CouponTemplate.ExpiredType expiredType, Integer couponLifetime) {
        if (expiredType == CouponTemplate.ExpiredType.DURATION_BASED){
            validateCouponLifetime(couponLifetime);
        }

    }

    private void validateFixedAmountType(Long fixedAmount){
        if(fixedAmount == null){
            throw new IllegalArgumentException("정액 할인 쿠폰은 할인값이 있어야 합니다");
        }

        if(fixedAmount <= 0){
            throw new IllegalArgumentException("정액 할인 쿠폰은 할인값이 0보다 커야합니다");
        }
    }

    private void validateFixedRateType(Long fixedRate){
        if(fixedRate == null){
            throw new IllegalArgumentException("정률 할인 쿠폰은 할인율이 있어야 합니다");
        }

        if(fixedRate <= 0 || fixedRate > 100){
            throw new IllegalArgumentException("정률 할인 쿠폰은 할인율이 0보다 크고 100 이하여야 합니다다");
        }
    }

    private void validateCouponLifetime(Integer couponLifetime){
        if(couponLifetime == null){
            throw new IllegalArgumentException("유효기간 타입의 쿠폰은 유효기간을 설정해야 합니다");
        }
        if(couponLifetime <= 0){
            throw new IllegalArgumentException("유효기간 타입의 쿠폰은 유효기간을 0보다 크게 설정 해야 합니다");
        }
    }

}
