package com.example.pillyohae.order.repository;

import com.example.pillyohae.order.dto.*;

import com.example.pillyohae.order.entity.QOrder;
import com.example.pillyohae.order.entity.QOrderProduct;
import com.example.pillyohae.product.entity.QProduct;
import com.example.pillyohae.user.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    private final QOrder order = QOrder.order;
    private final QOrderProduct orderProduct = QOrderProduct.orderProduct;
    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;
    private final QUser user = QUser.user;


    @Override
    public List<OrderPageResponseDto.OrderInfoDto> findOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize) {
        return queryFactory.select(new QOrderPageResponseDto_OrderInfoDto(order.id, order.status, order.orderName, order.paidAt, order.imageUrl, order.totalPrice))
                .from(order)
                .leftJoin(order.user)
                .where(dateEq(startAt, endAt), order.user.id.eq(userId))
                .orderBy(order.paidAt.desc())
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .fetch();


    }

    @Override
    public List<OrderPageSellerResponseDto.OrderInfoDto> findSellerOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize) {
        return queryFactory.select(new QOrderPageSellerResponseDto_OrderInfoDto(order.id, orderProduct.id ,orderProduct.status, order.orderName, order.paidAt, orderProduct.imageUrl, orderProduct.price, orderProduct.quantity))
                .from(orderProduct)
                .leftJoin(orderProduct.seller)
                .leftJoin(orderProduct.order, order)
                .where(dateEq(startAt, endAt), orderProduct.seller.id.eq(userId), order.paidAt.isNotNull())
                .orderBy(order.paidAt.desc())
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .fetch();



    }

    // orderitem에 저장된 내용을 가져옴
    @Override
    public List<OrderDetailResponseDto.OrderProductDto> findOrderProductsByOrderId(UUID orderId) {
        return queryFactory.select(new QOrderDetailResponseDto_OrderProductDto(orderProduct.id, orderProduct.productName, orderProduct.quantity, orderProduct.price, orderProduct.status))
                .from(orderProduct)
                .where(orderProduct.order.id.eq(orderId))
                .fetch();

    }

    @Override
    public OrderDetailResponseDto.OrderInfoDto findOrderDetailOrderInfoDtoByOrderId(UUID orderId) {
        return queryFactory.select(new QOrderDetailResponseDto_OrderInfoDto(order.id, order.status, order.orderName, order.totalPrice, order.paidAt, order.imageUrl, order.shippingAddress))
                .from(order)
                .where(order.id.eq(orderId))
                .fetchOne();


    }

    @Override
    public OrderDetailSellerResponseDto.OrderInfoDto findOrderDetailSellerInfoDtoByOrderId(UUID orderId) {
        return queryFactory.select(new QOrderDetailSellerResponseDto_OrderInfoDto(order.id, order.status, order.paidAt, order.shippingAddress))
                .from(orderProduct)
                .leftJoin(orderProduct.order, order)
                .where(order.id.eq(orderId))
                .fetchOne();

    }

    @Override
    public OrderDetailSellerResponseDto.OrderProductDto findOrderDetailSellerProductDtoByOrderId(UUID orderId, Long userId) {
        return queryFactory.select(new QOrderDetailSellerResponseDto_OrderProductDto(orderProduct.id,orderProduct.productName,orderProduct.quantity,orderProduct.price,orderProduct.status))
                .from(orderProduct)
                .leftJoin(orderProduct.order, order)
                .leftJoin(orderProduct.seller, user)
                .where(order.id.eq(orderId), user.id.eq(userId))
                .fetchOne();
    }

    private BooleanExpression dateEq(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null && endAt == null) {
            return null;
        } else if (startAt != null && endAt != null) {
            return order.paidAt.between(startAt, endAt);
        } else if (startAt != null) {
            return order.paidAt.goe(startAt);
        } else {
            return order.paidAt.loe(endAt);
        }
    }


}
