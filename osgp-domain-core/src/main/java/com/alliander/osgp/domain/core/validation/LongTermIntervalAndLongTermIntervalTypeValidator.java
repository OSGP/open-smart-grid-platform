package com.alliander.osgp.domain.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

import com.alliander.osgp.domain.core.valueobjects.Configuration;

public class LongTermIntervalAndLongTermIntervalTypeValidator implements
        ConstraintValidator<LongTermIntervalAndLongTermIntervalType, Configuration> {

    @Override
    public void initialize(final LongTermIntervalAndLongTermIntervalType constraintAnnotation) {
        // Nothing to do here
    }

    @Override
    public boolean isValid(final Configuration value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // If LongTermHistoryInterval or LongTermHistoryIntervalType is not
        // present,
        // the other must be not present as well.
        if (value.getLongTermHistoryInterval() == null && value.getLongTermHistoryIntervalType() == null) {
            return true;
        }

        // If LongTermHistoryInterval or LongTermHistoryIntervalType is present,
        // the other must be as well.
        if (value.getLongTermHistoryInterval() != null && value.getLongTermHistoryIntervalType() != null) {
            // And, the value of long term history interval must be among the
            // permitted values, defined by the ranges:
            // - from 1 to 30 for long term history interval type DAYS.
            // - from 1 to 12 for long term history interval type MONTHS.
            switch (value.getLongTermHistoryIntervalType()) {
            case DAYS:
                if (value.getLongTermHistoryInterval() >= 1 && value.getLongTermHistoryInterval() <= 30) {
                    return true;
                }
                break;
            case MONTHS:
                if (value.getLongTermHistoryInterval() >= 1 && value.getLongTermHistoryInterval() <= 12) {
                    return true;
                }
                break;
            default:
                throw new ValidationException("unknown LongTermHistoryIntervalType");

            }
        }

        return false;
    }
}
