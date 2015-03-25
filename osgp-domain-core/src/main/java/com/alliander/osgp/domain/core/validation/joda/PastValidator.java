package com.alliander.osgp.domain.core.validation.joda;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Past;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

public class PastValidator implements ConstraintValidator<Past, ReadableInstant> {

    @Override
    public void initialize(final Past constraintAnnotation) {
        // Empty Method
    }

    @Override
    public boolean isValid(final ReadableInstant value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        final DateMidnight checkDate = new DateMidnight(DateTimeZone.UTC);

        return value.isEqual(checkDate) || value.isBefore(checkDate);
    }
}
