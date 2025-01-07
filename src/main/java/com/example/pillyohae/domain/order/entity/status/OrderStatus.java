package com.example.pillyohae.domain.order.entity.status;

import lombok.Getter;

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
            return Set.of(); // 최종 상태로 전이 불가
        }
    },
    CANCELLED("취소됨") {
        @Override
        public Set<OrderStatus> getAllowedNextStatuses() {
            return Set.of(); // 최종 상태로 전이 불가
        }
    };

    private final String description;

    // Constructor
    OrderStatus(String description) {
        this.description = description;
    }

    // Abstract method for allowed transitions
    public abstract Set<OrderStatus> getAllowedNextStatuses();

    // Check if transition is allowed
    public boolean canTransitionTo(OrderStatus newStatus) {
        return getAllowedNextStatuses().contains(newStatus);
    }

    // Getter for description
    public String getDescription() {
        return description;
    }
}