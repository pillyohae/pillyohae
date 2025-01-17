package com.example.pillyohae.cart.repository;

import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.dto.QCartProductDetailResponseDto;
import com.example.pillyohae.cart.entity.QCart;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;


public class CartQueryRepositoryImpl implements CartQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QCart cart = QCart.cart;

    CartQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public List<CartProductDetailResponseDto> findCartDtoListByUserId(Long userId) {

        if (queryFactory == null) {
            throw new IllegalStateException("QueryFactory is not initialized");
        }

        return queryFactory.select(new QCartProductDetailResponseDto(cart.id, cart.product.productId, cart.product.productName, cart.product.imageUrl, cart.product.price, cart.quantity))
            .from(cart)
            .where(cart.user.id.eq(userId))
            .stream().toList();
    }


}
