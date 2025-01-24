package com.example.pillyohae.coupon.service;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final CouponTemplateRepository couponTemplateRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserRepository userRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected IssuedCoupon issueCoupon(UUID couponTemplateId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<IssuedCoupon> userIssuedCoupons = issuedCouponRepository.findIssuedCouponsWithTemplateByUserId(
                user.getId());

        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        checkDuplicateCoupon(userIssuedCoupons, couponTemplate);

        checkCouponQuantity(couponTemplate);

        checkCouponTemplateStatus(couponTemplate);

        couponTemplate.incrementIssuanceCount();

        IssuedCoupon issuedCoupon = new IssuedCoupon(LocalDateTime.now(), couponTemplate.getIssuedCouponExpiredAt(), couponTemplate, user);

        issuedCouponRepository.saveAndFlush(issuedCoupon);

        return issuedCoupon;
    }

    // 쿠폰 중복 검사
    private void checkDuplicateCoupon(List<IssuedCoupon> userIssuedCoupons, CouponTemplate couponTemplate) {
        List<CouponTemplate> userCouponTemplates = userIssuedCoupons.stream()
                .map(IssuedCoupon::getCouponTemplate).toList();

        if (userCouponTemplates.contains(couponTemplate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰을 중복해서 가질 수 없습니다");
        }
    }

    private void checkCouponQuantity(CouponTemplate couponTemplate) {
        if (couponTemplate.getCurrentIssuanceCount() >= couponTemplate.getMaxIssuanceCount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰 수량이 소진되었습니다");
        }
    }

    // 쿠폰 상태 검증
    private void checkCouponTemplateStatus(CouponTemplate couponTemplate) {
        if (couponTemplate.getStatus() != CouponTemplate.CouponStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용 가능한 쿠폰이 아닙니다. 현재 상태: " + couponTemplate.getStatus());
        }
    }
}
