package com.example.pillyohae.coupon.repository;

import com.example.pillyohae.coupon.dto.FindCouponListToUseResponseDto;
import com.example.pillyohae.coupon.dto.QFindCouponListToUseResponseDto_CouponInfo;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.QCouponTemplate;
import com.example.pillyohae.coupon.entity.QIssuedCoupon;
import com.example.pillyohae.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class IssuedCouponQueryRepositoryImpl implements IssuedCouponQueryRepository {
    private final QIssuedCoupon issuedCoupon = QIssuedCoupon.issuedCoupon;
    private final QCouponTemplate couponTemplate = QCouponTemplate.couponTemplate;
    private final QUser user = QUser.user;
    private final JPAQueryFactory queryFactory;

    // 유저가 가진 쿠폰중에 최소사용금액이 결제 총액보다 낮은경우를 찾음
    @Override
    public List<FindCouponListToUseResponseDto.CouponInfo> findCouponListToUse(Double price, Long userId) {
        return queryFactory
                .select(new QFindCouponListToUseResponseDto_CouponInfo(
                        issuedCoupon.id,
                        couponTemplate.name,
                        couponTemplate.description,
                        couponTemplate.type,
                        couponTemplate.fixedAmount,
                        couponTemplate.fixedRate,
                        couponTemplate.maxDiscountAmount,
                        couponTemplate.expiredAt
                ))
                .from(issuedCoupon)
                .join(issuedCoupon.couponTemplate, couponTemplate) // leftJoin -> join
                .join(issuedCoupon.user, user) // leftJoin -> join
                .where(
                        user.id.eq(userId),
                        couponTemplate.minimumPrice.loe(price), // goe -> loe로 수정 (최소사용금액이 결제금액보다 작아야 함)
                        couponTemplate.status.eq(CouponTemplate.CouponStatus.ACTIVE),
                        issuedCoupon.usedAt.isNull(), // 미사용 쿠폰 조건 추가
                        issuedCoupon.expiredAt.gt(LocalDateTime.now()) // 만료되지 않은 쿠폰 조건 추가
                )
                .orderBy(couponTemplate.minimumPrice.desc()) // 최소사용금액이 높은 순으로 정렬
                .fetch();
    }
}
