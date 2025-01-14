package com.example.pillyohae.coupon.entity;

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
@Table(name = "coupon_template", indexes = {
        @Index(name = "idx_coupon_template_status_price",
                columnList = "status,minimum_price")
})
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

    @Enumerated(EnumType.STRING)
    private ExpiredType expiredType;

    @PositiveOrZero
    private Double fixedAmount = 0.0;

    // %단위로 저장
    @PositiveOrZero
    @Max(100)
    private Double fixedRate = 0.0;

    @Column(nullable = false)
    @PositiveOrZero
    private Double minimumPrice = 0.0;

    @Column(nullable = false)
    @Positive
    private Double maxDiscountAmount;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

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
    public CouponTemplate(String name, String description, DiscountType type, Double fixedAmount, Double fixedRate, Double maxDiscountAmount, Double minimumPrice, LocalDateTime startAt, LocalDateTime expiredAt, Integer maxIssuanceCount) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.startAt = startAt;
        this.minimumPrice = minimumPrice;
        this.expiredAt = expiredAt;
        this.maxIssuanceCount = maxIssuanceCount;
        this.status = CouponStatus.ACTIVE;
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

    // 고정 날짜 만료 또는 생성일 기준 만료로 나뉨
    // 추후 기능 추가할 예정
    public enum ExpiredType {
        FIXED_DATE, DURATION_BASED
    }

    public enum DiscountType {
        FIXED_AMOUNT,
        PERCENTAGE
    }

    //INACTIVE 상태는 긴급하게 사용을 막아야할때 사용
    public enum CouponStatus {
        ACTIVE, INACTIVE
    }



}
