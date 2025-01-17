package com.example.pillyohae.global.validation;

import com.example.pillyohae.coupon.dto.CouponTemplateCreateRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// 검증 로직을 별도 클래스로 분리
public class ValidCouponPeriodValidator
        implements ConstraintValidator<ValidCouponPeriod, CouponTemplateCreateRequestDto> {

    @Override
    public boolean isValid(CouponTemplateCreateRequestDto dto,
                           ConstraintValidatorContext context) {
        if (dto.getStartAt() == null || dto.getExpiredAt() == null) {
            return false;
        }

        return dto.getStartAt().isBefore(dto.getExpiredAt());
    }
}