package com.example.pillyohae.order.entity;

import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    @Positive
    private Double price;

    @Column(nullable = false)
    @Positive
    private Long quantity;

    // 물품마다 다른 status를 갖는다
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    // 상점 주인의 주문 조회용
    // 연관 관계가 필요한지 고민
    @Column(nullable = false)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;


    public OrderItem(String productName, Double price, Long quantity, Long productId, Order order) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.productId = productId;
        this.order = order;
        // 초기 상태
        this.status = OrderItemStatus.PENDING;

        order.getOrderItems().add(this);
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
