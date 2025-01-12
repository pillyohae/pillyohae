package com.example.pillyohae.coupon.entity;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issued_coupon", indexes = {
        @Index(name = "idx_issued_coupon_user",
                columnList = "user_id,used_at,expire_at")
})
public class IssuedCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime issuedDate;       // 발급일

    private LocalDateTime usedAt;         // 사용일

    @Enumerated(EnumType.STRING)
    private CouponStatus status;            // 쿠폰 상태

    private LocalDateTime expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id")
    private CouponTemplate couponTemplate;  // 쿠폰 정보

    @OneToOne(fetch = FetchType.LAZY)
    private Order usedOrder;                // 사용된 주문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;              // 쿠폰 소유 사용자

    public IssuedCoupon(LocalDateTime issuedDate,CouponTemplate couponTemplate, User user) {
        this.issuedDate = issuedDate;
        this.couponTemplate = couponTemplate;
        this.user = user;
        this.status = CouponStatus.AVAILABLE;
    }

    // 쿠폰 상태 enum
    public enum CouponStatus {
        AVAILABLE,    // 사용 가능
        USED,        // 사용됨
        EXPIRED,      // 만료됨
        CANCELED     // 취소됨
        }
}