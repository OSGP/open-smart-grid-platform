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
import org.opensmartgridplatform.domain.core.valueobjects.LongTermIntervalType;

public class LongTermIntervalAndLongTermIntervalTypeValidator
        implements ConstraintValidator<LongTermIntervalAndLongTermIntervalType, Configuration> {

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
        // present, the other must be not present as well.
        if (value.getLongTermHistoryInterval() == null && value.getLongTermHistoryIntervalType() == null) {
            return true;
        }
        // And, the value of long term history interval must be among the
        // permitted values, defined by the ranges:
        // - from 1 to 30 for long term history interval type DAYS.
        // - from 1 to 12 for long term history interval type MONTHS.
        final int interval = value.getLongTermHistoryInterval();
        final LongTermIntervalType type = value.getLongTermHistoryIntervalType();
        return this.checkRanges(interval, type);
    }

    private boolean checkRanges(final int interval, final LongTermIntervalType type) {
        switch (type) {
        case DAYS:
            if (interval >= 1 && interval <= 30) {
                return true;
            }
            break;
        case MONTHS:
            if (interval >= 1 && interval <= 12) {
                return true;
            }
            break;
        default:
            throw new ValidationException("unknown LongTermHistoryIntervalType");

        }

        return false;
    }
}
