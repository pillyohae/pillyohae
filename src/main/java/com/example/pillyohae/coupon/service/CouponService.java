package com.example.pillyohae.coupon.service;

import com.example.pillyohae.coupon.dto.CreateCouponTemplateRequestDto;
import com.example.pillyohae.coupon.dto.CreateCouponTemplateResponseDto;
import com.example.pillyohae.coupon.dto.GiveCouponResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserService userService;


    @Transactional
    public CreateCouponTemplateResponseDto createCouponTemplate(String email,
        CreateCouponTemplateRequestDto requestDto) {
        User user = userService.findByEmail(email);
        // 추후에 admin으로 변경해야함
        if (user.getRole() != Role.SELLER) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자만 쿠폰을 만들 수 있습니다.");
        }
        CouponTemplate couponTemplate = CouponTemplate.builder()
            .name(requestDto.getCouponName())
            .type(requestDto.getDiscountType())
            .description(requestDto.getCouponDescription())
            .fixedAmount(requestDto.getFixedAmount())
            .fixedRate(requestDto.getFixedRate())
            .maxDiscountAmount(requestDto.getMaxDiscountAmount())
            .startAt(requestDto.getStartAt())
            .expireAt(requestDto.getExpireAt())
            .maxIssuanceCount(requestDto.getMaxIssueCount())
            .build();
        couponTemplateRepository.save(couponTemplate);
        return new CreateCouponTemplateResponseDto(couponTemplate.getId());
    }

    @Transactional
    public GiveCouponResponseDto giveCoupon(String email, Long couponTemplateId) {
        User user = userService.findByEmail(email);
        if (user.getRole() != Role.BUYER) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "BUYER만 받을 수 있습니다");
        }
        // issued coupon과 coupon template을 한번에 가져옴
        List<IssuedCoupon> userIssuedCoupons = issuedCouponRepository.findIssuedCouponsWithTemplateByUserId(
            user.getId());
        List<CouponTemplate> userCouponTemplates = userIssuedCoupons.stream()
            .map(IssuedCoupon::getCouponTemplate).toList();
        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (userCouponTemplates.contains(couponTemplate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쿠폰을 중복해서 가질 수 없습니다");
        }
        IssuedCoupon issuedCoupon = new IssuedCoupon(LocalDateTime.now(), couponTemplate, user);
        issuedCouponRepository.save(issuedCoupon);
        return new GiveCouponResponseDto(issuedCoupon.getId());
    }


    @Transactional
    public void issueCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> updatedCouponIds = couponTemplateRepository.findTemplateIdsByStartAtAndNowState(
            now,
            CouponTemplate.CouponStatus.INACTIVE.toString()
        );
        couponTemplateRepository.updateTemplateStatus(updatedCouponIds,
            CouponTemplate.CouponStatus.ACTIVE.toString());
    }

    @Transactional
    public void expireCouponByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<Long> couponTemplates = expireCouponTemplateBetweenTimes(start, end);
        expireIssuedCoupon(couponTemplates);
    }

    @Transactional
    public void expireCoupon() {
        List<Long> updatedCouponTemplateIds = expireCouponTemplate();
        expireIssuedCoupon(updatedCouponTemplateIds);
    }

    @Transactional
    protected void expireIssuedCoupon(List<Long> updatedCouponTemplateIds) {
        int maxAttempts = 10; // 최대 시도 횟수
        int attempt = 0;
        int batchSize = 1000; // 배치 사이즈
        boolean hasExpiredCoupons = true;
        LocalDateTime now = LocalDateTime.now();
        // 최대 10000개를 처리함
        // template마다 실행
        for (Long updatedCouponTemplateId : updatedCouponTemplateIds) {
            hasExpiredCoupons = true;
            attempt = 0;
            while (hasExpiredCoupons && attempt < maxAttempts) {
                // couponTemplate을 통해 만료시킴
                int updatedCount = issuedCouponRepository.updateIssuedCouponStatusByCouponTemplate_Id(
                    updatedCouponTemplateId,
                    IssuedCoupon.CouponStatus.EXPIRED.toString(),
                    IssuedCoupon.CouponStatus.AVAILABLE.toString(),
                    batchSize
                );

                int remainCouponCount = issuedCouponRepository.countIssuedCouponByCouponTemplate_Id(
                    updatedCouponTemplateId);

                hasExpiredCoupons = remainCouponCount != 0;
                attempt++;

                if (hasExpiredCoupons) {
                    alertRemainCoupon(remainCouponCount, attempt, updatedCount);
                }
            }
            if (hasExpiredCoupons) {
                failExpireAllCoupon(maxAttempts);
                // 알림 발송 또는 다른 처리
            }
        }

    }

    @Transactional
    protected List<Long> expireCouponTemplateBetweenTimes(LocalDateTime start, LocalDateTime end) {
        int maxAttempts = 3; // 최대 시도 횟수
        int attempt = 0;
        int batchSize = 10; // 배치 사이즈
        boolean hasExpiredCoupons = true;
        LocalDateTime now = LocalDateTime.now();
        List<Long> updatedCouponAllIds = new ArrayList<>();

        // 최대 30개를 처리함
        // 최대 3회 시도 한번에 10개 처리
        while(hasExpiredCoupons && attempt < maxAttempts) {
            // 만료시킬 쿠폰의 식별자를 batch 크기만큼 가져옴
            List<Long> updatedCouponIds = couponTemplateRepository.findExpiredTemplateIdsBetweenTimes(
                    start,
                    end,
                    CouponTemplate.CouponStatus.EXPIRED.toString(),
                    batchSize
            );
            // 식별자 리스트를 통해 모두 만료시킴
            int updatedCount = couponTemplateRepository.updateTemplateStatus(
                    updatedCouponIds,
                    CouponTemplate.CouponStatus.EXPIRED.toString()
            );
            updatedCouponAllIds.addAll(updatedCouponIds);

            // 만료되지 않은 쿠폰을 확인
            int remainCouponCount = couponTemplateRepository.countByExpireAtBetween(start, end);


            hasExpiredCoupons = remainCouponCount != 0;
            attempt++;

            if(hasExpiredCoupons) {
                alertRemainCoupon(remainCouponCount, attempt, updatedCount);
            }
        }
        if(hasExpiredCoupons) {
            failExpireAllCoupon(maxAttempts);
            // 알림 발송 또는 다른 처리
        }

        return updatedCouponAllIds;
    }

    @Transactional
    protected List<Long> expireCouponTemplate() {
        int maxAttempts = 3; // 최대 시도 횟수
        int attempt = 0;
        int batchSize = 100; // 배치 사이즈
        boolean hasExpiredCoupons = true;
        LocalDateTime now = LocalDateTime.now();

        List<Long> updatedCouponAllIds = new ArrayList<>();

        // 최대 300개를 처리함
        // 최대 3회 시도 한번에 100개 처리
        while(hasExpiredCoupons && attempt < maxAttempts) {
            // 만료시킬 쿠폰의 식별자를 batch 크기만큼 가져옴
            List<Long> updatedCouponIds = couponTemplateRepository.findExpiredTemplateIds(
                    now,
                    CouponTemplate.CouponStatus.EXPIRED.toString(),
                    batchSize
            );
            // 식별자 리스트를 통해 모두 만료시킴
            int updatedCount = couponTemplateRepository.updateTemplateStatus(
                    updatedCouponIds,
                    CouponTemplate.CouponStatus.EXPIRED.toString()
            );
            updatedCouponAllIds.addAll(updatedCouponIds);

            // 만료되지 않은 쿠폰을 확인
            int remainCouponCount = couponTemplateRepository.countByExpireAt(now);

            hasExpiredCoupons = remainCouponCount != 0;
            attempt++;

            if(hasExpiredCoupons) {
                alertRemainCoupon(remainCouponCount, attempt, updatedCount);
            }
        }
        if(hasExpiredCoupons) {
            failExpireAllCoupon(maxAttempts);
            // 알림 발송 또는 다른 처리
        }

        return updatedCouponAllIds;
    }

    private static void alertRemainCoupon(int count, int attempt, int updatedCount) {
        log.warn("Still have {} expired coupons after attempt {}. Updated {} coupons.",
                count, attempt, updatedCount);
    }

    private static void failExpireAllCoupon(int maxAttempts) {
        log.error("Failed to expire all coupons after {} attempts", maxAttempts);
    }




}
