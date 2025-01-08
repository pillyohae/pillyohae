package com.example.pillyohae.order.entity;

import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.example.pillyohae.order.entity.status.OrderStatus;
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
    private Double totalPrice = 0.0;

    // order 전반적인 status
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private String orderName;

    // 주문 생성 후 실제 결제가 되고나서 값이 지정됨
    @Column
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
    public void updateStatus(OrderStatus newStatus) {
        if (status.canTransitionTo(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException(
                    "현재 상태(" + status.getValue() + ")에서 " + newStatus.getValue() + " 상태로 변경할 수 없습니다."
            );
        }
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


    public Double updateTotalPrice() {
        this.totalPrice = 0.0;
        for (OrderItem item : this.orderItems) {
            this.totalPrice += item.getPrice();
        }
        return this.totalPrice;
    }

}
