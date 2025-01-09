package com.example.pillyohae.product.repository;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.QProduct;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.example.pillyohae.product.entity.QProduct.product;

@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {

        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public Page<Product> getAllProduct(String productName, String companyName, String category, Pageable pageable) {

        List<OrderSpecifier<?>> orders = getSortOrders(pageable, product);

        List<Product> content = jpaQueryFactory
            .selectFrom(product)
            .where(
                productNameContains(productName),
                companyNameEq(companyName),
                categoryEq(category)
            )
            .offset(pageable.getOffset()) // 몇 번째 페이지부터 시작할 것 인지.
            .limit(pageable.getPageSize())// 페이지당 몇개의 데이터를 보여줄껀지
            .orderBy(orders.toArray(new OrderSpecifier<?>[0]))
            .fetch();

        JPAQuery<Long> total = jpaQueryFactory
            .select(product.count())
            .from(product)
            .where(
                productNameContains(productName),
                companyNameEq(companyName),
                categoryEq(category)
            );

        // PageableExecutionUtils를 사용하여 Page 객체 반환
        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    private List<OrderSpecifier<?>> getSortOrders(Pageable pageable, QProduct product) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        // 기본 정렬 (DESC)
        if (pageable.getSort().isEmpty()) {
            orders.add(product.productId.desc());
            return orders;
        }

        // 동적 정렬 처리
        for (Sort.Order sortOrder : pageable.getSort()) {
            boolean isAscending = sortOrder.isAscending();
            String property = sortOrder.getProperty();

            // 동적으로 정렬 기준 생성
            ComparableExpressionBase<?> fieldPath = getComparableFieldPath(property, product);
            Order order = isAscending ? Order.ASC : Order.DESC;

            orders.add(new OrderSpecifier<>(order, fieldPath));
        }
        return orders;
    }

    private ComparableExpressionBase<?> getComparableFieldPath(String property, QProduct product) {
        switch (property) {
            case "productId":
                return product.productId;
            case "price":
                return product.price;
            default:
                throw new CustomResponseStatusException(ErrorCode.NOT_FOUND_PROPERTY);
        }
    }


    private BooleanExpression categoryEq(String category) {
        return category != null ? product.category.eq(category) : null;
    }

    private BooleanExpression companyNameEq(String companyName) {
        return companyName != null ? product.companyName.eq(companyName) : null;
    }

    private BooleanExpression productNameContains(String productName) {
        return productName != null ? product.productName.contains(productName) : null;
    }
}








