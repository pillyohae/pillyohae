package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.dto.BuyerOrderDetailInfo;
import com.example.pillyohae.order.dto.BuyerOrderInfo;

import com.example.pillyohae.order.dto.QBuyerOrderDetailInfo_BuyerOrderItemInfo;
import com.example.pillyohae.order.dto.QBuyerOrderInfo;
import com.example.pillyohae.order.entity.QOrder;
import com.example.pillyohae.order.entity.QOrderItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    private static final QOrder order = QOrder.order;
    private static final QOrderItem orderItem = QOrderItem.orderItem;
    private final JPAQueryFactory queryFactory;

    public OrderQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<BuyerOrderInfo> findBuyerOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize) {
        if (queryFactory == null) {
            throw new IllegalStateException("QueryFactory is not initialized");
        }
        return queryFactory.select(new QBuyerOrderInfo(order.id, order.status, order.orderName, order.paidAt))
                .from(order)
                .leftJoin(order.user)
                .where(dateEq(startAt, endAt), order.user.id.eq(userId))
                .orderBy(order.paidAt.desc())
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<BuyerOrderDetailInfo.BuyerOrderItemInfo> findBuyerOrderDetail(UUID orderId){
        if (queryFactory == null) {
            throw new IllegalStateException("QueryFactory is not initialized");
        }

        return queryFactory.select(new QBuyerOrderDetailInfo_BuyerOrderItemInfo(orderItem.id, orderItem.productName, orderItem.quantity, order.totalPrice, orderItem.status))
                .where(orderItem.order.id.eq(orderId))
                .fetch();

    }

    private BooleanExpression dateEq(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null && endAt == null) {
            return null;
        }
        if (startAt == null) {
            return order.paidAt.before(endAt);
        }
        if (endAt == null) {
            return order.paidAt.after(startAt);
        }
        return order.paidAt.between(startAt, endAt);
    }


}
