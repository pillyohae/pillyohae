package com.example.pillyohae.domain.order.entity;

import com.example.pillyohae.domain.order.entity.status.OrderItemStatus;
import com.example.pillyohae.domain.order.entity.status.OrderStatus;
import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

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

    @Column(nullable = false)
    private Double totalPrice;

    // order 전반적인 status
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private LocalDateTime payTime;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Order(User user) {
        this.user = user;
        // 초기 상태 결제 대기중
        this.status = OrderStatus.PENDING;
    }
    // order에 대한 status 업데이트
    public OrderStatus updateStatus(OrderStatus newStatus) {
        if (canTransitionTo(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException("현재 상태에서는 " + newStatus.getDescription() + " 상태로 변경할 수 없습니다.");
        }
        return status;
    }

    // order 품목별 status 업데이트

    public OrderItemStatus updateItemStatus(Long itemId, OrderItemStatus newStatus) {
        for (OrderItem item : this.orderItems) {
            if (item.getId().equals(itemId)) {
                item.updateStatus(newStatus);
                return item.getStatus();
            }
        }
        throw new IllegalArgumentException("해당 ID의 품목을 찾을 수 없습니다: " + itemId);
    }


    // 주문은 결제와 취소만 존재 상세 주문 및 배송상태는 각 아이템별로 존재
    private boolean canTransitionTo(OrderStatus newStatus) {
        switch (this.status) {
            case PENDING:
                return newStatus == OrderStatus.PAYMENT_CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case PAYMENT_CONFIRMED:
            case CANCELLED:
                return false; // 최종 상태는 변경 불가
            default:
                return false;
        }
    }

    public Double updateTotalPrice() {
        for (OrderItem item : this.orderItems) {
            this.totalPrice += item.getPrice();
        }
        return this.totalPrice;
    }

}
