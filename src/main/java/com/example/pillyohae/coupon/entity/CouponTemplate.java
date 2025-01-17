package com.example.pillyohae.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@DynamicInsert
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpiredType expiredType;

    @PositiveOrZero
    private Long fixedAmount;

    // %단위로 저장
    @PositiveOrZero
    @Min(0) @Max(100)
    private Long fixedRate;

    @Column(nullable = false)
    @PositiveOrZero
    private Long minimumPrice;

    //만약 최대 할인금액을 설정하지 않았을경우 금액제한이 없다고 가정
    //default 값이 들어가도록 설정
    @Column(nullable = false)
    @Positive
    private Long maxDiscountAmount;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private Integer maxIssuanceCount;

    @Column(nullable = false)
    private Integer currentIssuanceCount;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @Column
    private Period couponLifetime;

    @Version  // 낙관적 락을 위한 버전 필드
    private Long version;


    /**
     * 사용자에게 발급된 쿠폰 목록
     * 만료되지 않은 활성 쿠폰들을 포함합니다
     */
    @OneToMany(mappedBy = "couponTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssuedCoupon> issuedCoupons = new ArrayList<>();

    @Builder
    public CouponTemplate(String name, String description, DiscountType discountType, ExpiredType expiredType, Long fixedAmount, Long fixedRate, Long maxDiscountAmount, Long minimumPrice, LocalDateTime startAt, LocalDateTime expiredAt, Integer maxIssuanceCount, Integer couponLifetime) {
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
        if(couponLifetime == null) {
            couponLifetime = 0;
        }
        this.couponLifetime = Period.ofDays(couponLifetime);

        this.status = CouponStatus.ACTIVE;
        this.currentIssuanceCount = 0;
    }

    public LocalDateTime getIssuedCouponExpiredAt() {

        if (this.expiredAt == null || this.startAt == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"쿠폰 만료일자 또는 시작일자가 존재하지 않습니다.");
        }

        if(ExpiredType.FIXED_DATE == this.expiredType){
            return this.expiredAt;
        }

        if (ExpiredType.DURATION_BASED == this.expiredType) {
            return LocalDateTime.now().plus(couponLifetime);
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"쿠폰 만료일자 타입이 FIXED_DATE 또는 DURATION_BASED 가 아닙니다");
    }

    public synchronized void incrementIssuanceCount() {
        validateTemplate();
        validateIssuanceLimit();

        this.currentIssuanceCount++;

        if (this.currentIssuanceCount.equals(this.maxIssuanceCount)) {
            this.status = CouponStatus.INACTIVE;
        }

    }
    // 쿠폰 상태 검증
    private void validateTemplate() {
        if (status != CouponStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"사용 가능한 쿠폰이 아닙니다. 현재 상태: " + status);
        }
    }
    // 쿠폰 한도 검증
    private void validateIssuanceLimit() {
        if (currentIssuanceCount >= maxIssuanceCount) {
            this.status = CouponStatus.INACTIVE;
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"쿠폰 발급 한도(" + maxIssuanceCount + "개)를 초과했습니다.");
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
