package com.example.pillyohae.cart.repository;

import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.dto.QCartProductDetailResponseDto;
import com.example.pillyohae.cart.entity.QCart;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CartQueryRepositoryImpl implements CartQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QCart cart = QCart.cart;

    @Override
    public List<CartProductDetailResponseDto> findCartDtoListByUserId(Long userId) {
        return queryFactory.select(new QCartProductDetailResponseDto(cart.id, cart.product.productId, cart.product.productName, cart.product.imageUrl, cart.product.price, cart.quantity))
            .from(cart)
            .where(cart.user.id.eq(userId))
            .stream().toList();
    }


}
