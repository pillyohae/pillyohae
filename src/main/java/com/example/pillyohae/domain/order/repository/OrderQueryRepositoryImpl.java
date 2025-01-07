package com.example.pillyohae.domain.order.repository;

import com.example.pillyohae.domain.order.dto.BuyerOrderInfo;
import com.example.pillyohae.domain.order.dto.QBuyerOrderInfo;
import com.example.pillyohae.domain.order.entity.QOrder;
import com.example.pillyohae.domain.order.entity.QOrderItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;

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
        return queryFactory.select(new QBuyerOrderInfo(order.id, order.status, order.orderName, order.payTime))
                .from(order)
                .leftJoin(order.user)
                .where(dateEq(startAt, endAt), order.user.id.eq(userId))
                .orderBy(order.payTime.desc())
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .fetch();
    }


    private BooleanExpression dateEq(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null && endAt == null) {
            return null;
        }
        if (startAt == null) {
            return order.payTime.before(endAt);
        }
        if (endAt == null) {
            return order.payTime.after(startAt);
        }
        return order.payTime.between(startAt, endAt);
    }


}
