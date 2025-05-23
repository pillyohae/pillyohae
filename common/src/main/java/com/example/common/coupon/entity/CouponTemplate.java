package com.example.common.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 쿠폰 템플릿 엔터티. 쿠폰의 기본 정보, 할인 유형, 유효 기간, 최대 발급 수 등을 관리한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
@Table(name = "coupon_template", indexes = {
    @Index(name = "idx_coupon_template_status_price", columnList = "status,minimum_price")
})
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 쿠폰 ID

    @Column(nullable = false)
    private String name; // 쿠폰 이름

    @Column(nullable = false)
    private String description; // 쿠폰 설명

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // 할인 유형 (정액, 퍼센트)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpiredType expiredType; // 만료 유형 (고정일, 기간 기준)

    @PositiveOrZero
    private Long fixedAmount; // 정액 할인 금액

    @PositiveOrZero
    @Min(0)
    @Max(100)
    private Long fixedRate; // 퍼센트 할인율

    @Column(nullable = false)
    @PositiveOrZero
    private Long minimumPrice; // 최소 사용 가능 금액

    @Column(nullable = false)
    @Positive
    private Long maxDiscountAmount; // 최대 할인 가능 금액

    @Column(nullable = false)
    private LocalDateTime startAt; // 쿠폰 사용 시작 시간

    @Column(nullable = false)
    private LocalDateTime expiredAt; // 쿠폰 만료 시간

    @Column(nullable = false)
    private Integer maxIssuanceCount; // 최대 발급 가능 수

    @Column(nullable = false)
    private Integer currentIssuanceCount; // 현재 발급 수

    @Enumerated(EnumType.STRING)
    private CouponStatus status; // 쿠폰 상태 (활성, 비활성)

    @Column
    private Integer couponLifetime; // 발급 후 유효 기간 (기간 기반 만료 시 사용)

    @Column
    private Boolean isDeleted = Boolean.FALSE; // 삭제 여부

    @OneToMany(mappedBy = "couponTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssuedCoupon> issuedCoupons = new ArrayList<>(); // 발급된 쿠폰 목록

    /**
     * 쿠폰 템플릿 생성자.
     */
    @Builder
    public CouponTemplate(String name, String description, DiscountType discountType,
        ExpiredType expiredType, Long fixedAmount, Long fixedRate,
        Long maxDiscountAmount, Long minimumPrice, LocalDateTime startAt,
        LocalDateTime expiredAt, Integer maxIssuanceCount,
        Integer couponLifetime) {
        this.name = name;
        this.description = description;
        this.discountType = discountType;
        this.expiredType = expiredType;
        this.fixedAmount = fixedAmount;
        this.fixedRate = fixedRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.startAt = startAt;
        this.minimumPrice = minimumPrice;
        this.expiredAt = expiredAt;
        this.maxIssuanceCount = maxIssuanceCount;
        this.couponLifetime = couponLifetime != null ? couponLifetime : 0;
        this.status = CouponStatus.ACTIVE;
        this.currentIssuanceCount = 0;
        this.isDeleted = false;
    }

    public LocalDateTime getIssuedCouponExpiredAt() {

        if (this.expiredAt == null || this.startAt == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "쿠폰 만료일자 또는 시작일자가 존재하지 않습니다.");
        }

        if (ExpiredType.FIXED_DATE == this.expiredType) {
            return this.expiredAt;
        }

        if (ExpiredType.DURATION_BASED == this.expiredType) {
            return LocalDateTime.now().plus(Period.ofDays(couponLifetime));
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "쿠폰 만료일자 타입이 FIXED_DATE 또는 DURATION_BASED 가 아닙니다");
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void incrementIssuanceCount() {

        this.currentIssuanceCount++;
    }

    public void updateStatus(CouponStatus status) {
        this.status = status;
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
