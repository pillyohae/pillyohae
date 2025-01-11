package com.example.pillyohae.Coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//발급할 쿠폰의 정보 저장
public class CouponTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    @PositiveOrZero
    private Double fixedAmount = 0.0;

    @PositiveOrZero @Max(100)
    private Double fixedRate = 0.0;

    @Column(nullable = false)
    @Positive
    private Double maxDiscountAmount;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    @Column(nullable = false)
    private Integer maxIssuanceCount;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;
    /**
      * 사용자에게 발급된 쿠폰 목록
      * 만료되지 않은 활성 쿠폰들을 포함합니다
      */
    @OneToMany(mappedBy = "couponTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssuedCoupon> issuedCoupons = new ArrayList<>();

    @Builder
    public CouponTemplate(String name, String description, DiscountType type, Double fixedAmount, Double fixedRate, Double maxDiscountAmount, LocalDateTime startAt, LocalDateTime expireAt, Integer maxIssuanceCount) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.startAt = startAt;
        this.expireAt = expireAt;
        this.maxIssuanceCount = maxIssuanceCount;
        this.status = CouponStatus.INACTIVE;
    }

    @PrePersist
    @PreUpdate
    private void ensureDefaultValues() {
        if (this.fixedAmount == null) {
            this.fixedAmount = 0.0;
        }
        if (this.fixedRate == null) {
            this.fixedRate = 0.0;
        }
    }

    public enum DiscountType {
        FIXED_AMOUNT,
        PERCENTAGE
    }

    public enum CouponStatus{
        ACTIVE, INACTIVE, EXPIRED
    }





}