package com.example.pillyohae.cart.repository;

import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.dto.QCartProductDetailResponseDto;
import com.example.pillyohae.cart.entity.QCart;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.pillyohae.product.entity.QProductImage.productImage;

@RequiredArgsConstructor
public class CartQueryRepositoryImpl implements CartQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QCart cart = QCart.cart;

    @Override
    public List<CartProductDetailResponseDto> findCartDtoListByUserId(Long userId) {
        return queryFactory
            .select(new QCartProductDetailResponseDto
                (cart.id,
                    cart.product.productId,
                    cart.product.productName,
                    productImage.fileUrl,
                    cart.product.price,
                    cart.quantity
                ))
            .from(cart)
            .leftJoin(productImage)
            .on(productImage.product.eq(cart.product)) // cart와 productImage를 조인
            .where(cart.user.id.eq(userId)
                .and(
                    productImage.position.eq(0)
                        .or(
                            productImage.position.eq(1).and(
                                JPAExpressions.selectOne()
                                    .from(productImage)
                                    .where(productImage.product.eq(cart.product)
                                        .and(productImage.position.eq(0))
                                    )
                                    .notExists()
                            )
                        )
                )
            )
            .stream().toList();
    }

}
