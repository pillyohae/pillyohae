package com.example.pillyohae.coupon.repository;

import com.example.pillyohae.coupon.dto.CouponListResponseDto;
import com.example.pillyohae.coupon.dto.QCouponListResponseDto_CouponInfo;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.QCouponTemplate;
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
    public List<CouponListResponseDto.CouponInfo> findCouponList(CouponTemplate.CouponStatus status) {
        if (queryFactory == null) {
            throw new IllegalStateException("QueryFactory is not initialized");
        }
        return queryFactory.select(new QCouponListResponseDto_CouponInfo(couponTemplate.id,couponTemplate.name,
                        couponTemplate.description, couponTemplate.discountType,couponTemplate.fixedAmount
                        ,couponTemplate.fixedRate, couponTemplate.maxDiscountAmount,couponTemplate.minimumPrice,couponTemplate.expiredAt))
                .from(couponTemplate)
                .where(statusEq(status), couponTemplate.expiredAt.after(LocalDateTime.now()))
                .fetch();
    }


    private BooleanExpression statusEq(CouponTemplate.CouponStatus status) {
        if (status == null) {
            return null;
        }
        return couponTemplate.status.eq(status);
    }


}
