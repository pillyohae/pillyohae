package com.example.pillyohae.order.entity.status;

import java.util.Set;

public enum OrderStatus {
    PENDING("대기 중") {
        @Override
        public Set<OrderStatus> getAllowedNextStatuses() {
            return Set.of(PAYMENT_CONFIRMED, CANCELLED);
        }
    },
    PAYMENT_CONFIRMED("결제 완료") {
        @Override
        public Set<OrderStatus> getAllowedNextStatuses() {
            return Set.of(CANCELLED);
        }
    },
    CANCELLED("취소됨") {
        @Override
        public Set<OrderStatus> getAllowedNextStatuses() {
            return Set.of(); // 최종 상태로 전이 불가
        }
    };

    private final String value;

    // Constructor
    OrderStatus(String value) {
        this.value = value;
    }

    // Abstract method for allowed transitions
    public abstract Set<OrderStatus> getAllowedNextStatuses();

    // Check if transition is allowed
    public boolean canTransitionTo(OrderStatus newStatus) {
        return getAllowedNextStatuses().contains(newStatus);
    }

    // Getter for description
    public String getValue() {
        return value;
    }
}