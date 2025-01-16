package com.example.pillyohae.order.entity;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.global.entity.address.ShippingAddress;
import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.example.pillyohae.order.entity.status.OrderStatus;
import com.example.pillyohae.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // snapshot 결제 완료후 저장
    @Column(nullable = false)
    private Long totalPrice;
    // snapshot 결제 완료후 저장
    private Long discountAmount;
    // snapshot 결제 완료후 저장
    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private String imageUrl;

    // 주문 생성 후 실제 결제가 되고나서 값이 지정됨
    @Column
    private LocalDateTime paidAt;

    // order 전반적인 status
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    //배송지 정보 저장
    @Embedded
    private ShippingAddress shippingAddress;

    // 주문 물품
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    // 사용된 쿠폰
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_coupon_id")
    private IssuedCoupon issuedCoupon;

    // 구매자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    // 결제를 위한 주문 생성
    public Order(User user, ShippingAddress shippingAddress, String imageUrl, Long totalPrice, String orderName) {
        this.user = user;
        this.shippingAddress = shippingAddress;
        this.imageUrl = imageUrl;
        this.totalPrice = totalPrice;
        this.orderName = orderName;
        // 초기 상태 결제 대기중
        this.status = OrderStatus.PENDING;
    }

    // 쿠폰 유효 확인 후 적용
    public void applyCoupon(IssuedCoupon issuedCoupon) {
        validateCouponToUse(issuedCoupon,this.user);

        Long discountAmount = calculateDiscountAmount(issuedCoupon);

        this.issuedCoupon = issuedCoupon;

        this.discountAmount = discountAmount;

        issuedCoupon.useCoupon(this);
    }

    private Long calculateDiscountAmount(IssuedCoupon issuedCoupon) {

        Long tempDiscountAmount;

        if (CouponTemplate.DiscountType.FIXED_AMOUNT.equals(issuedCoupon.getCouponTemplate().getDiscountType())) {

            tempDiscountAmount = issuedCoupon.getCouponTemplate().getFixedAmount();
        } else {
            // fixed rate는 %단위
            tempDiscountAmount = totalPrice * issuedCoupon.getCouponTemplate().getFixedRate() / 100;
        }

        Long maxDiscount = issuedCoupon.getCouponTemplate().getMaxDiscountAmount();

        if (maxDiscount < tempDiscountAmount) {

            tempDiscountAmount = maxDiscount;
        }

        if(tempDiscountAmount < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "할인 가격이 음수가 될 수 없습니다");
        }

        return tempDiscountAmount;
    }

    // order에 대한 status 업데이트
    public void updateStatus(OrderStatus newStatus) {
        if (this.status == null) {
            throw new IllegalStateException("현재 주문 상태가 없습니다.");
        }
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("현재 상태(%s)에서 %s 상태로 변경할 수 없습니다.",
                            status.getValue(), newStatus.getValue())
            );
        }
        this.status = newStatus;
    }

    // order 품목별 status 업데이트
    public OrderProductStatus updateProductStatus(Long itemId, OrderProductStatus newStatus) {

        for (OrderProduct item : this.orderProducts) {

            if (item.getId().equals(itemId)) {

                item.updateStatus(newStatus);

                return item.getStatus();
            }
        }

        throw new IllegalArgumentException("해당 ID의 품목을 찾을 수 없습니다: " + itemId);
    }



    // 쿠폰이 만료되거나 사용될 경우 또는 쿠폰 사용을 금지했을경우 예외
    private void validateCouponToUse(IssuedCoupon coupon, User user) {

        if (coupon == null || coupon.getCouponTemplate() == null) {
            throw new IllegalArgumentException("유효하지 않은 쿠폰입니다");
        }

        if(CouponTemplate.CouponStatus.INACTIVE.equals(coupon.getCouponTemplate().getStatus())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coupon is not active");
        }

        if (!coupon.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Coupon is not owned by user");
        }

        if (LocalDateTime.now().isAfter(coupon.getExpiredAt())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Coupon is expired");
        }

        if (IssuedCoupon.CouponStatus.USED.equals(coupon.getStatus())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Coupon is used");
        }

    }


}
