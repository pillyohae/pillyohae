package com.example.pillyohae.domain.order.entity.status;

import java.util.EnumSet;
import java.util.Set;

import java.util.Set;

public enum OrderItemStatus {
    PENDING("주문 접수 중") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(CANCELLED, READY_FOR_SHIPMENT);
        }
    },
    READY_FOR_SHIPMENT("배송 준비 완료") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(SHIPPED);
        }
    },
    SHIPPED("배송 중") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(DELIVERED, DELIVERY_FAILED);
        }
    },
    DELIVERED("배송 완료") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(RETURN_REQUESTED);
        }
    },
    RETURN_REQUESTED("반품 요청됨") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(RETURNED);
        }
    },
    RETURNED("반품 완료") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(); // 전이 불가
        }
    },
    DELIVERY_FAILED("배송 실패") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(); // 전이 불가
        }
    },
    CANCELLED("취소됨") {
        @Override
        public Set<OrderItemStatus> getAllowedNextStatuses() {
            return Set.of(); // 전이 불가
        }
    };

    private final String description;

    // Constructor
    OrderItemStatus(String description) {
        this.description = description;
    }

    // Abstract method for allowed next statuses
    public abstract Set<OrderItemStatus> getAllowedNextStatuses();

    // Check if transition is allowed
    public boolean canTransitionTo(OrderItemStatus nextStatus) {
        return getAllowedNextStatuses().contains(nextStatus);
    }

    // Getter for description
    public String getDescription() {
        return description;
    }
}
