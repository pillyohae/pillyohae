package com.example.pillyohae.order.entity;

import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // snapshot 결제 완료후 저장
    @Column(nullable = false)
    private String productName = "초기값";

    // snapshot 결제 완료후 저장
    @Positive
    private Long price;

    @Column(nullable = false)
    @Positive
    private Integer quantity;

    // 물품마다 다른 status를 갖는다
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    // 구매완료전 상품의 실시간 정보를 위해 필요
    @Column(nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 결제 하기전에 상품 갯수 및 식별정보 및 주문 식별 정보만 설정
    public OrderProduct(Integer quantity, Long productId, Order order) {
        this.quantity = quantity;
        this.productId = productId;
        this.order = order;
        // 초기 상태
        this.status = OrderItemStatus.PENDING;
        order.getOrderProducts().add(this);
    }

    // Update status with validation
    public void updateStatus(OrderItemStatus newStatus) {
        if (status.canTransitionTo(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException(
                    "현재 상태(" + status.getValue() + ")에서 " + newStatus.getValue() + " 상태로 변경할 수 없습니다."
            );
        }
    }


    public void setSeller(User seller) {
        this.seller = seller;
        if (seller != null && !seller.getSellerOrders().contains(this)) {
            seller.getSellerOrders().add(this);
        }
    }

}
