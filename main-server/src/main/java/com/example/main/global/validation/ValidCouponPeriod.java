package com.example.main.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCouponPeriodValidator.class)
public @interface ValidCouponPeriod {
    String message() default "쿠폰 유효 기간이 올바르지 않습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}