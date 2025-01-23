package com.example.pillyohae.product.repository;

import static com.example.pillyohae.product.entity.QProduct.product;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.QProduct;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.recommendation.dto.RecommendationKeywordDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QueryFactory queryFactory;

    public ProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory, QueryFactory queryFactory) {

        this.jpaQueryFactory = jpaQueryFactory;
        this.queryFactory = queryFactory;
    }

    /**
     * 추천 상품 조회용
     *
     * @param keywords 추천 상품 키워드
     * @return 추천 상품 목록
     */
    @Override
    public List<Product> findProductsByNameLike(RecommendationKeywordDto[] keywords) {

        List<Product> queries = new ArrayList<>();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.status.eq(ProductStatus.SELLING));

        // 각 키워드별 한 개씩 조회
        for (RecommendationKeywordDto keyword : keywords) {
            Product result = jpaQueryFactory.select(product)
                .from(product)
                .where(product.productName.contains(keyword.getRecommendation()))
                .where(builder)
                .limit(1)
                .fetchOne();

            if (result != null) {
                queries.add(result);
            }
        }

        return queries;
    }


    @Override
    public Page<Product> getAllProduct(String productName, String companyName, String category, Pageable pageable) {

        List<OrderSpecifier<?>> orders = getSortOrders(pageable, product);

        List<Product> content = jpaQueryFactory
            .selectFrom(product)
            .where(
                productNameContains(productName),
                companyNameEq(companyName),
                categoryEq(category),
                product.deletedAt.isNull()
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
                categoryEq(category),
                product.deletedAt.isNull()
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

            // 동적으로 정렬 기준 생성 // 정렬 필드와 방향을 기반으로 OrderSpecifier 생성
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








