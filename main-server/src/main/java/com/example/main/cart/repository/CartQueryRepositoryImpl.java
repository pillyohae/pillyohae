package com.example.main.cart.repository;

import com.example.common.cart.entity.QCart;
import com.example.common.product.entity.QProduct;
import com.example.main.cart.dto.CartProductDetailResponseDto;
import com.example.main.cart.dto.QCartProductDetailResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.common.product.entity.QProductImage.productImage;

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
            .on(productImageJoinCondition(cart.product))
            .where(cart.user.id.eq(userId))
            .fetch();
    }

    private BooleanExpression productImageJoinCondition(QProduct product) {
        return productImage.product.productId.eq(product.productId)
            .and(productImage.position.eq(0)
                .or(productImage.position.eq(1)
                    .and(thumbnailExists(product)))
            );
    }

    private BooleanExpression thumbnailExists(QProduct product) {
        return JPAExpressions.select(productImage.fileUrl)
            .from(productImage)
            .where(productImage.product.productId.eq(product.productId)
                .and(productImage.position.eq(0))
            )
            .notExists();
    }

}
