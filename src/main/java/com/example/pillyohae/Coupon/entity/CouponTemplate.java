package com.example.pillyohae.Coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
//발급할 쿠폰의 정보 저장
public class CouponTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    @Positive
    private Double fixedAmount;

    @Positive @Max(100)
    private Double fixedRate;

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


    public enum DiscountType {
        FIXED_AMOUNT,
        PERCENTAGE
    }

    public enum CouponStatus{
        ACTIVE, INACTIVE
    }





}
