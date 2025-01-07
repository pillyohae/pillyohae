package com.example.pillyohae.domain.order.entity;

import com.example.pillyohae.domain.order.entity.status.OrderItemStatus;
import com.example.pillyohae.domain.order.entity.status.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String productName;

    @Column
    private Double price;

    @Column
    private Long quantity;

    // 물품마다 다른 status를 갖는다
    @Column
    private OrderItemStatus status;


    // 상점 주인의 주문 조회용
    // 연관 관계가 필요한지 고민
    @Column
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
    }

    public void updateStatus(OrderItemStatus newStatus) {
        this.status = newStatus;
    }

    private boolean canTransitionTo(OrderItemStatus newStatus) {
        switch (this.status) {
            case PENDING:
                return newStatus == OrderItemStatus.READY_FOR_SHIPMENT || newStatus == OrderItemStatus.CANCELLED;
            case READY_FOR_SHIPMENT:
                return newStatus == OrderItemStatus.SHIPPED;
            case SHIPPED:
                return newStatus == OrderItemStatus.DELIVERED || newStatus == OrderItemStatus.DELIVERY_FAILED;
            case DELIVERED:
                return newStatus == OrderItemStatus.RETURN_REQUESTED;
            case RETURN_REQUESTED:
                return newStatus == OrderItemStatus.RETURNED;
            case DELIVERY_FAILED:
            case CANCELLED:
                return false; // 최종 상태는 변경 불가
            default:
                return false;
        }
    }
}
