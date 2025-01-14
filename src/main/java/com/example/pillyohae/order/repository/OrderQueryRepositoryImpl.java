package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.dto.BuyerOrderDetailInfo;
import com.example.pillyohae.order.dto.BuyerOrderInfo;
import com.example.pillyohae.order.dto.QBuyerOrderDetailInfo_BuyerOrderProductInfo;
import com.example.pillyohae.order.dto.QBuyerOrderInfo;
import com.example.pillyohae.order.entity.QOrder;
import com.example.pillyohae.order.entity.QOrderProduct;
import com.example.pillyohae.product.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    private final QOrder order = QOrder.order;
    private final QOrderProduct orderProduct = QOrderProduct.orderProduct;
    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;

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

    // orderitem에 저장된 내용을 가져옴
    @Override
    public List<BuyerOrderDetailInfo.BuyerOrderProductInfo> findBuyerOrderDetailAfterPayment(UUID orderId) {
        if (queryFactory == null) {
            throw new IllegalStateException("QueryFactory is not initialized");
        }

        return queryFactory.select(new QBuyerOrderDetailInfo_BuyerOrderProductInfo(orderProduct.id, orderProduct.productName, orderProduct.quantity, orderProduct.price, orderProduct.status))
                .where(orderProduct.order.id.eq(orderId))
                .fetch();

    }

    // 실시간으로 product로부터 정보를 가져옴
    @Override
    public List<BuyerOrderDetailInfo.BuyerOrderProductInfo> findBuyerOrderDetailBeforePayment(UUID orderId) {
        if (queryFactory == null) {
            throw new IllegalStateException("QueryFactory is not initialized");
        }

        return queryFactory
                .select(new QBuyerOrderDetailInfo_BuyerOrderProductInfo(
                        orderProduct.id,
                        product.productName,
                        orderProduct.quantity,
                        product.price,
                        orderProduct.status))
                .from(orderProduct)
                .innerJoin(product)
                .on(orderProduct.productId.eq(product.productId))
                .where(orderProduct.order.id.eq(orderId))  // 단일 조건으로 수정
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
