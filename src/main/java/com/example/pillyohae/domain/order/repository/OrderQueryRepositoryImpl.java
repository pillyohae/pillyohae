package com.example.pillyohae.domain.order.repository;

import com.example.pillyohae.domain.order.dto.BuyerOrderInfo;
import com.example.pillyohae.domain.order.dto.QBuyerOrderInfo;
import com.example.pillyohae.domain.order.entity.QOrder;
import com.example.pillyohae.domain.order.entity.QOrderItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    QOrder order = QOrder.order;
    QOrderItem orderItem = QOrderItem.orderItem;
    JPAQueryFactory queryFactory;
    @Override
    public List<BuyerOrderInfo> findBuyerOrderInfoListByUserIdAndDate(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize) {
        return queryFactory.select(new QBuyerOrderInfo(order.id,order.status, order.orderName, order.payTime))
                .from(order)
                .leftJoin(order.user)
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .where(dateEq(startAt, endAt), order.user.id.eq(userId))
                .fetch();
    }



    private BooleanExpression dateEq(LocalDateTime startAt, LocalDateTime endAt) {
        if(startAt == null && endAt == null) {
            return null;
        }
        if(startAt == null) {
            return order.payTime.before(endAt);
        }
        if(endAt == null) {
            return order.payTime.after(startAt);
        }
        return order.payTime.between(startAt, endAt);
    }


}
