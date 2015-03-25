package com.alliander.osgp.domain.core.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Pattern(regexp = "[^ ]{0,40}")
@Size(min = 1, max = 40)
@ReportAsSingleViolation
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface Identification {
    public abstract String message() default "Invalid identification";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}
