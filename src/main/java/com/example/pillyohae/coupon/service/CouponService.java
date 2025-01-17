package com.example.pillyohae.coupon.service;

import com.example.pillyohae.coupon.dto.CreateCouponTemplateRequestDto;
import com.example.pillyohae.coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.coupon.dto.FindCouponListResponseDto;
import com.example.pillyohae.coupon.dto.GiveCouponResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;


    @Transactional
    public CreateCouponTemplateResponseDto createCouponTemplate(CreateCouponTemplateRequestDto requestDto) {

        CouponTemplate couponTemplate = CouponTemplate.builder()
                .name(requestDto.getCouponName())
                .discountType(requestDto.getDiscountType())
                .expiredType(requestDto.getExpiredType())
                .description(requestDto.getCouponDescription())
                .fixedAmount(requestDto.getFixedAmount())
                .fixedRate(requestDto.getFixedRate())
                .maxDiscountAmount(requestDto.getMaxDiscountAmount())
                .startAt(requestDto.getStartAt())
                .expiredAt(requestDto.getExpiredAt())
                .maxIssuanceCount(requestDto.getMaxIssueCount())
                .minimumPrice(validateMinimumPrice(requestDto.getMinimumPrice()))
                .couponLifetime(requestDto.getCouponLifetime())
                .build();

        couponTemplateRepository.save(couponTemplate);

        return new CreateCouponTemplateResponseDto(couponTemplate.getId());
    }

    // 유저 개인이 발행
    @Transactional
    public GiveCouponResponseDto giveCoupon(String email, Long couponTemplateId) {

        User user = userService.findByEmail(email);
        // issued coupon과 coupon template을 한번에 가져옴
        List<IssuedCoupon> userIssuedCoupons = issuedCouponRepository.findIssuedCouponsWithTemplateByUserId(
                user.getId());

        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        validDuplicateCoupon(userIssuedCoupons, couponTemplate);

        IssuedCoupon issuedCoupon = issueCoupon(couponTemplate, user);

        return new GiveCouponResponseDto(issuedCoupon.getId());
    }

    private synchronized IssuedCoupon issueCoupon(CouponTemplate couponTemplate, User user) {
        if(couponTemplate.getStartAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아직 발급 시작이 안되었습니다");
        }
        IssuedCoupon issuedCoupon;
        try {
            couponTemplate.incrementIssuanceCount();
            couponTemplateRepository.saveAndFlush(couponTemplate);
            issuedCoupon= new IssuedCoupon(LocalDateTime.now(), couponTemplate.getIssuedCouponExpiredAt(), couponTemplate, user);
            issuedCouponRepository.save(issuedCoupon);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"발급중 오류가 발생하였습니다");
        }

        return issuedCoupon;
    }

    private void validDuplicateCoupon(List<IssuedCoupon> userIssuedCoupons, CouponTemplate couponTemplate) {
        List<CouponTemplate> userCouponTemplates = userIssuedCoupons.stream()
                .map(IssuedCoupon::getCouponTemplate).toList();

        if (userCouponTemplates.contains(couponTemplate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰을 중복해서 가질 수 없습니다");
        }
    }


    @Transactional
    public FindCouponListResponseDto findCouponListToUse(String email, Long totalPrice) {
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "로그인이 되어야 합니다");
        }
        User user = userService.findByEmail(email);
        return new FindCouponListResponseDto(
                issuedCouponRepository.findCouponListByPriceAndUserId(totalPrice, user.getId())
        );
    }

    private Long validateMinimumPrice(Long minimumPrice) {
        if (minimumPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 주문 금액은 필수입니다");
        }
        if (minimumPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 주문 금액은 0 이상이어야 합니다");
        }
        return minimumPrice;
    }

}
