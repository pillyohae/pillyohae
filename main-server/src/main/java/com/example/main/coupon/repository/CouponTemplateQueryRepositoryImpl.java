package com.example.main.coupon.repository;

import com.example.common.coupon.entity.CouponTemplate;
import com.example.common.coupon.entity.QCouponTemplate;
import com.example.main.coupon.dto.CouponTemplateListResponseDto;
import com.example.main.coupon.dto.QCouponTemplateListResponseDto_CouponInfo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CouponTemplateQueryRepositoryImpl implements CouponTemplateQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QCouponTemplate couponTemplate = QCouponTemplate.couponTemplate;

    @Override
    public List<CouponTemplateListResponseDto.CouponInfo> findCouponList(CouponTemplate.CouponStatus status) {
        if (queryFactory == null) {
            throw new IllegalStateException("QueryFactory is not initialized");
        }
        return queryFactory.select(new QCouponTemplateListResponseDto_CouponInfo(couponTemplate.id,couponTemplate.name,
                        couponTemplate.description, couponTemplate.discountType,couponTemplate.fixedAmount
                        ,couponTemplate.fixedRate, couponTemplate.maxDiscountAmount,couponTemplate.minimumPrice, couponTemplate.expiredType, couponTemplate.expiredAt,couponTemplate.couponLifetime,couponTemplate.status))
                .from(couponTemplate)
                .where(statusEq(status), couponTemplate.expiredAt.after(LocalDateTime.now()), couponTemplate.isDeleted.eq(false))
                .fetch();




    }


    private BooleanExpression statusEq(CouponTemplate.CouponStatus status) {
        if (status == null) {
            return null;
        }
        return couponTemplate.status.eq(status);
    }


}
