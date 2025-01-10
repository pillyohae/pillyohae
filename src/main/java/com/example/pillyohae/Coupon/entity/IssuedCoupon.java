package com.example.pillyohae.Coupon.entity;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class IssuedCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime issuedDate;       // 발급일

    private LocalDateTime usedDate;         // 사용일

    @Enumerated(EnumType.STRING)
    private CouponStatus status;            // 쿠폰 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id")
    private CouponTemplate couponTemplate;  // 쿠폰 정보

    @OneToOne(fetch = FetchType.LAZY)
    private Order usedOrder;                // 사용된 주문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;              // 쿠폰 소유 사용자

    // 쿠폰 상태 enum
    public enum CouponStatus {
        AVAILABLE,    // 사용 가능
        USED,        // 사용됨
        EXPIRED,      // 만료됨
        CANCELED     // 취소됨
        }
}