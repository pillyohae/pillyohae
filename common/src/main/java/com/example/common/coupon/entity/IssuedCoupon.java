package com.example.common.coupon.entity;

import com.example.common.order.entity.Order;
import com.example.common.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 발급된 쿠폰 엔터티. 특정 사용자가 발급받은 쿠폰 정보를 관리한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issued_coupon", indexes = {
    @Index(name = "idx_issued_coupon_user",
        columnList = "user_id,used_at,expired_at")
})
public class IssuedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime issuedAt;       // 발급일

    private LocalDateTime usedAt;         // 사용일

    @Enumerated(EnumType.STRING)
    private CouponStatus status;            // 쿠폰 상태

    @Column(nullable = false)
    private LocalDateTime expiredAt; // 유저 각각의 쿠폰 만료일자 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id")
    private CouponTemplate couponTemplate;  // 쿠폰 정보

    @OneToOne(fetch = FetchType.LAZY)
    private Order usedOrder;                // 사용된 주문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;              // 쿠폰 소유 사용자

    public IssuedCoupon(LocalDateTime issuedAt, LocalDateTime expiredAt,
        CouponTemplate couponTemplate, User user) {
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.couponTemplate = couponTemplate;
        this.user = user;
        this.status = CouponStatus.AVAILABLE;
        validateExpireAt();
    }

    // 쿠폰을 사용할 때 만료여부 확인
    public void validateExpireAt() {
        if (expiredAt == null) {

            throw new IllegalArgumentException("만료일은 필수 값입니다.");
        }

        if (expiredAt.isBefore(issuedAt)) {

            throw new IllegalArgumentException("만료일은 발급일 이후여야 합니다.");
        }
    }

    //사용한 쿠폰에 정보 저장
    public void useCoupon(Order order) {

        this.usedAt = LocalDateTime.now();

        this.usedOrder = order;

        this.status = CouponStatus.USED;
    }

    // 쿠폰 상태는 사용가능한 상태와 사용된 상태만 존재 만료 여부는 expiredAt으로 판단
    public enum CouponStatus {
        AVAILABLE,    // 사용 가능
        USED,        // 사용됨
    }
}

