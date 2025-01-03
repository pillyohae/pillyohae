package com.example.pillyohae.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DueDateValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDueDate {
    String message() default "마감 일자는 현재 시간보다 늦어야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
