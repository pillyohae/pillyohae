package com.example.pillyohae.global.validation;

import com.example.pillyohae.Coupon.dto.CreateCouponTemplateRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// 검증 로직을 별도 클래스로 분리
public class ValidCouponPeriodValidator
        implements ConstraintValidator<ValidCouponPeriod, CreateCouponTemplateRequestDto> {

    @Override
    public boolean isValid(CreateCouponTemplateRequestDto dto,
                           ConstraintValidatorContext context) {
        if (dto.getStartAt() == null || dto.getExpireAt() == null) {
            return false;
        }

        return dto.getStartAt().isBefore(dto.getExpireAt());
    }
}