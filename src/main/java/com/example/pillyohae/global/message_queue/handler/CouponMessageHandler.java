package com.example.pillyohae.global.message_queue.handler;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.coupon.service.CouponService;
import com.example.pillyohae.global.message_queue.message.CouponMessage;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponMessageHandler implements DomainMessageHandler<CouponMessage> {

    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;
    private final UserRepository userRepository;

    @Override
    public void handle(Object message) {

        CouponMessage couponMessage = (CouponMessage) message;

        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponMessage.getCouponTemplateId())
                .orElseThrow(
                        () -> new RuntimeException("CouponTemplate not found")
                );
        User user = userRepository.findById(couponMessage.getUserId())
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );
        // 주문 관련 비즈니스 로직 처리
        IssuedCoupon issuedCoupon = new IssuedCoupon(couponMessage.getIssuedCouponId(),couponMessage.getIssueTime(),couponTemplate.getIssuedCouponExpiredAt(),couponTemplate,user);

        issuedCouponRepository.save(issuedCoupon);

    }

    @Override
    public Class<CouponMessage> supportedType() {
        return CouponMessage.class;
    }
}
