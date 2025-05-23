package com.example.main.order.repository;

import com.example.common.order.entity.QOrder;
import com.example.common.order.entity.QOrderProduct;
import com.example.common.product.entity.QProduct;
import com.example.common.user.entity.QUser;
import com.example.main.order.dto.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    public Page<OrderInfoDto> findOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable) {
        List<OrderInfoDto> content = queryFactory
                .select(new QOrderInfoDto(
                        order.id,
                        order.status,
                        order.orderName,
                        order.paidAt,
                        order.imageUrl,
                        order.totalPrice
                ))
                .from(order)
                .leftJoin(order.user)
                .where(dateEq(startAt, endAt),
                        order.user.id.eq(userId))
                .orderBy(order.paidAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리
        long total = queryFactory
                .select(order.count())
                .from(order)
                .where(dateEq(startAt, endAt),
                        order.user.id.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<OrderSellerInfoDto> findSellerOrders(Long userId, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable) {
        List<OrderSellerInfoDto> content = queryFactory.select(
                        new QOrderSellerInfoDto(
                                orderProduct.id,
                                orderProduct.status,
                                order.orderName,
                                order.paidAt,
                                order.shippingAddress,
                                orderProduct.imageUrl,
                                orderProduct.price,
                                orderProduct.quantity
                        ))
                .from(orderProduct)
                .leftJoin(orderProduct.seller)
                .leftJoin(orderProduct.order, order)
                .where(dateEq(startAt, endAt), orderProduct.seller.id.eq(userId), order.paidAt.isNotNull())
                .orderBy(order.paidAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리
        long total = queryFactory
                .select(orderProduct.count())
                .from(orderProduct)
                .leftJoin(orderProduct.seller)
                .leftJoin(orderProduct.order, order)
                .where(dateEq(startAt, endAt), orderProduct.seller.id.eq(userId), order.paidAt.isNotNull())
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // orderitem에 저장된 내용을 가져옴
    @Override
    public List<OrderDetailResponseDto.OrderProductDto> findOrderProductsByOrderId(UUID orderId) {
        return queryFactory.select(new QOrderDetailResponseDto_OrderProductDto(orderProduct.id, orderProduct.productName, orderProduct.quantity, orderProduct.price, orderProduct.status, orderProduct.imageUrl))
                .from(orderProduct)
                .where(orderProduct.order.id.eq(orderId))
                .fetch();


    }

    @Override
    public OrderDetailResponseDto.OrderInfoDto findOrderDetailOrderInfoDtoByOrderId(UUID orderId) {
        return queryFactory.select(new QOrderDetailResponseDto_OrderInfoDto(order.id, order.status, order.orderName, order.totalPrice, order.paidAt, order.shippingAddress))
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
                .fetchFirst();

    }

    @Override
    public List<OrderDetailSellerResponseDto.OrderProductDto> findOrderDetailSellerProductDtoByOrderId(UUID orderId, Long userId) {
        return queryFactory.select(new QOrderDetailSellerResponseDto_OrderProductDto(orderProduct.id,orderProduct.productName,orderProduct.quantity,orderProduct.price,orderProduct.status))
                .from(orderProduct)
                .leftJoin(orderProduct.order, order)
                .leftJoin(orderProduct.seller, user)
                .where(order.id.eq(orderId), user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<OrderProductFetchJoinProduct> findOrderProductWithProduct(UUID orderId){
        return queryFactory
                .select(new QOrderProductFetchJoinProduct(orderProduct, product))
                .from(orderProduct)
                .join(product)
                .on(orderProduct.productId.eq(product.productId)) // 조인 조건 명시
                .where(orderProduct.order.id.eq(orderId)) // 필터 조건
                .fetch();
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
