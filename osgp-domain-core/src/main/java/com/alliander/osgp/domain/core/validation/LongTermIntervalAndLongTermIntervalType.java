package com.alliander.osgp.domain.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LongTermIntervalAndLongTermIntervalTypeValidator.class)
@Documented
public @interface LongTermIntervalAndLongTermIntervalType {

    String message() default "LongTermInterval and LongTermIntervalType must both be omitted or both be present. Further the permitted range for LongTermIntervalType.DAYS is from 1 to 30 and for LongTermIntervalType.MONTHS is from 1 to 12.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}