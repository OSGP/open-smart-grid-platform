/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.opensmartgridplatform.domain.core.valueobjects.Configuration;

public class ShortTermHistoryIntervalMinutesValidator implements
        ConstraintValidator<ShortTermHistoryIntervalMinutes, Configuration> {

    private final List<Integer> values = new ArrayList<Integer>();

    @Override
    public void initialize(final ShortTermHistoryIntervalMinutes arg0) {
        // Permitted values are null (not set), 15 minutes, 30 minutes, 60
        // minutes, 240 minutes.
        this.values.add(null);
        this.values.add(15);
        this.values.add(30);
        this.values.add(60);
        this.values.add(240);
    }

    @Override
    public boolean isValid(final Configuration value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return this.values.contains(value.getShortTermHistoryIntervalMinutes());
    }
}
