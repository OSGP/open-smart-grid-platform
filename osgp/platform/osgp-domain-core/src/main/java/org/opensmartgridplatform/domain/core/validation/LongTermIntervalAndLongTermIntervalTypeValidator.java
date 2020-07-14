/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

import org.opensmartgridplatform.domain.core.valueobjects.Configuration;

public class LongTermIntervalAndLongTermIntervalTypeValidator
        implements ConstraintValidator<LongTermIntervalAndLongTermIntervalType, Configuration> {

    @Override
    public void initialize(final LongTermIntervalAndLongTermIntervalType constraintAnnotation) {
        // Nothing to do here
    }

    @Override
    public boolean isValid(final Configuration value, final ConstraintValidatorContext context) {
        if (value == null)
            return true;

        // LongTermHistoryInterval and LongTermHistoryIntervalType must
        // either be: BOTH PRESENT or BOTH NOT PRESENT
        // Using the exclusive or operator this becomes
        if (value.getLongTermHistoryInterval() == null ^ value.getLongTermHistoryIntervalType() == null)
            return false;

        // And, the value of long term history interval must be among the
        // permitted values
        // First check upper limit depending on interval type:
        // - maximal 30 for long term history interval type DAYS.
        // - maximal 12 for long term history interval type MONTHS.
        switch (value.getLongTermHistoryIntervalType()) {
        case DAYS:
            if (value.getLongTermHistoryInterval() > 30)
                return false;
            break;
        case MONTHS:
            if (value.getLongTermHistoryInterval() > 12)
                return false;
            break;
        default:
            throw new ValidationException("unknown LongTermHistoryIntervalType");
            return false;
        }

        // Check lower limit:
        // - minimal 1
        if (value.getLongTermHistoryInterval() < 1)
            return false;

        // All validation tests passed correctly
        return true;
    }
}
