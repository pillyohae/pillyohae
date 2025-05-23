package com.example.main.coupon.service;

import com.example.common.coupon.entity.CouponTemplate;
import com.example.common.coupon.entity.IssuedCoupon;
import com.example.common.user.entity.User;
import com.example.main.coupon.dto.*;
import com.example.main.coupon.repository.CouponTemplateRepository;
import com.example.main.coupon.repository.IssuedCouponRepository;
import com.example.main.global.distributedLock.DistributedLock;
import com.example.main.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 쿠폰 관련 비즈니스 로직을 처리하는 서비스 클래스. 쿠폰 템플릿 생성, 쿠폰 발급, 쿠폰 사용, 쿠폰 상태 변경 등의 기능을 수행함.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserService userService;

    /**
     * 새로운 쿠폰 템플릿을 생성하는 메서드.
     *
     * @param requestDto 쿠폰 생성 요청 정보를 담은 DTO
     * @return 생성된 쿠폰 템플릿 정보를 포함하는 응답 DTO
     */
    @Transactional
    public CreateCouponTemplateResponseDto createCouponTemplate(
        CouponTemplateCreateRequestDto requestDto) {

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

        return new CreateCouponTemplateResponseDto(couponTemplate.getName(),
            couponTemplate.getDescription(),
            couponTemplate.getDiscountType(), couponTemplate.getExpiredType(),
            couponTemplate.getFixedAmount(),
            couponTemplate.getFixedRate(), couponTemplate.getMaxDiscountAmount(),
            couponTemplate.getMinimumPrice(),
            couponTemplate.getMaxIssuanceCount(), couponTemplate.getStartAt(),
            couponTemplate.getExpiredAt(),
            couponTemplate.getCouponLifetime());
    }

    /**
     * 특정 쿠폰을 특정 사용자에게 발급하는 메서드.
     *
     * @param email            쿠폰을 받을 사용자의 이메일
     * @param couponTemplateId 발급할 쿠폰 템플릿 ID
     * @return 발급된 쿠폰 정보를 담은 응답 DTO
     */
    @DistributedLock(key = "'couponTemplateId:' + #couponTemplateId", waitTime = 10L, leaseTime = 10L)
    public CouponGiveResponseDto giveCoupon(String email, UUID couponTemplateId) {
        IssuedCoupon issuedCoupon;
        try {
            issuedCoupon = issueCoupon(couponTemplateId, email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        CouponTemplate couponTemplate = issuedCoupon.getCouponTemplate();

        return new CouponGiveResponseDto(issuedCoupon.getId(), couponTemplate.getName(),
            couponTemplate.getDescription(),
            couponTemplate.getDiscountType(), couponTemplate.getFixedAmount(),
            couponTemplate.getFixedRate(),
            couponTemplate.getMaxDiscountAmount(), couponTemplate.getMinimumPrice(),
            couponTemplate.getIssuedCouponExpiredAt());
    }

    /**
     * 사용자가 결제 시 사용할 수 있는 쿠폰 목록을 조회하는 메서드.
     *
     * @param email      사용자 이메일
     * @param totalPrice 주문 총액
     * @return 사용 가능한 쿠폰 목록을 포함하는 DTO
     */
    @Transactional
    public CouponListResponseDto findCouponListToUse(String email, Long totalPrice) {
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "로그인이 되어야 합니다");
        }
        User user = userService.findByEmail(email);
        return new CouponListResponseDto(
            issuedCouponRepository.findCouponListByPriceAndUserId(totalPrice, user.getId())
        );
    }

    /**
     * 쿠폰의 상태를 업데이트하는 메서드.
     *
     * @param couponTemplateId 쿠폰 템플릿 ID
     * @param couponStatus     변경할 쿠폰 상태
     * @return 변경된 쿠폰 상태를 포함하는 응답 DTO
     */
    @Transactional
    public CouponUpdateStatusResponseDto updateCouponStatus(UUID couponTemplateId,
                                                            CouponTemplate.CouponStatus couponStatus) {
        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        couponTemplate.updateStatus(couponStatus);

        return new CouponUpdateStatusResponseDto(couponTemplate.getStatus(),
            couponTemplate.getId());
    }

    /**
     * 특정 쿠폰을 삭제하는 메서드.
     *
     * @param couponTemplateId 삭제할 쿠폰의 ID
     */
    @Transactional
    public void deleteCoupon(UUID couponTemplateId) {
        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        couponTemplate.delete();
    }

    /**
     * 사용자가 특정 쿠폰을 발급받는 메서드.
     *
     * @param couponTemplateId 발급할 쿠폰의 템플릿 ID
     * @param email            사용자 이메일
     * @return 발급된 쿠폰 객체
     */
    protected IssuedCoupon issueCoupon(UUID couponTemplateId, String email) {
        User user = userService.findByEmail(email);

        List<IssuedCoupon> userIssuedCoupons = issuedCouponRepository.findIssuedCouponsWithTemplateByUserId(
            user.getId());

        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        checkDuplicateCoupon(userIssuedCoupons, couponTemplate);
        checkCouponQuantity(couponTemplate);
        checkCouponTemplateStatus(couponTemplate);

        couponTemplate.incrementIssuanceCount();

        IssuedCoupon issuedCoupon = new IssuedCoupon(LocalDateTime.now(),
            couponTemplate.getIssuedCouponExpiredAt(), couponTemplate, user);

        issuedCouponRepository.saveAndFlush(issuedCoupon);

        return issuedCoupon;
    }

    /**
     * 사용자가 이미 해당 쿠폰을 가지고 있는지 확인하는 메서드.
     *
     * @param userIssuedCoupons 사용자가 가진 쿠폰 목록
     * @param couponTemplate    중복 확인할 쿠폰 템플릿
     */
    private void checkDuplicateCoupon(List<IssuedCoupon> userIssuedCoupons,
        CouponTemplate couponTemplate) {
        List<CouponTemplate> userCouponTemplates = userIssuedCoupons.stream()
            .map(IssuedCoupon::getCouponTemplate).toList();

        if (userCouponTemplates.contains(couponTemplate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰을 중복해서 가질 수 없습니다");
        }
    }

    /**
     * 쿠폰 발급 가능한 수량을 확인하는 메서드.
     *
     * @param couponTemplate 확인할 쿠폰 템플릿
     */
    private void checkCouponQuantity(CouponTemplate couponTemplate) {
        if (couponTemplate.getCurrentIssuanceCount() >= couponTemplate.getMaxIssuanceCount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰 수량이 소진되었습니다");
        }
    }

    /**
     * 쿠폰의 상태가 ACTIVE인지 확인하는 메서드.
     *
     * @param couponTemplate 확인할 쿠폰 템플릿
     */
    private void checkCouponTemplateStatus(CouponTemplate couponTemplate) {
        if (couponTemplate.getStatus() != CouponTemplate.CouponStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "사용 가능한 쿠폰이 아닙니다. 현재 상태: " + couponTemplate.getStatus());
        }
    }

    /**
     * 최소 주문 금액이 올바른지 확인하는 메서드.
     *
     * @param minimumPrice 최소 주문 금액
     * @return 검증된 최소 주문 금액
     */
    private Long validateMinimumPrice(Long minimumPrice) {
        if (minimumPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 주문 금액은 필수입니다");
        }
        if (minimumPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 주문 금액은 0 이상이어야 합니다");
        }
        return minimumPrice;
    }

    /**
     * 특정 상태의 쿠폰 목록을 조회하는 메서드.
     *
     * @param status 조회할 쿠폰 상태
     * @return 쿠폰 목록을 포함하는 DTO
     */
    public CouponTemplateListResponseDto findCouponList(CouponTemplate.CouponStatus status) {
        List<CouponTemplateListResponseDto.CouponInfo> couponTemplates = couponTemplateRepository.findCouponList(
            status);
        return new CouponTemplateListResponseDto(couponTemplates);
    }
}

