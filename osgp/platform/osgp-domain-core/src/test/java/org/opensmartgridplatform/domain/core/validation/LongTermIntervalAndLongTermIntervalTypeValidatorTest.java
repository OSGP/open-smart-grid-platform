/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.LongTermIntervalType;

public class LongTermIntervalAndLongTermIntervalTypeValidatorTest {

    private LongTermIntervalAndLongTermIntervalTypeValidator validator = new LongTermIntervalAndLongTermIntervalTypeValidator();

    private Configuration createConfigurationInstance(final int interval, final LongTermIntervalType type) {
        return new Configuration.Builder().withLongTermHistoryInterval(interval)
                .withLongTermHistoryIntervalType(type)
                .build();
    }

    @Test
    public void testValidDayLowerRange() {
        final Configuration input = this.createConfigurationInstance(1, LongTermIntervalType.DAYS);

        assertThat(this.validator.isValid(input, null)).isTrue();
    }

    @Test
    public void testValidDayUpperRange() {
        final Configuration input = this.createConfigurationInstance(30, LongTermIntervalType.DAYS);

        assertThat(this.validator.isValid(input, null)).isTrue();
    }

    @Test
    public void testInvalidDayLowerRange() {
        final Configuration input = this.createConfigurationInstance(0, LongTermIntervalType.DAYS);

        assertThat(this.validator.isValid(input, null)).isFalse();
    }

    @Test
    public void testInvalidDayUpperRange() {
        final Configuration input = this.createConfigurationInstance(31, LongTermIntervalType.DAYS);

        assertThat(this.validator.isValid(input, null)).isFalse();
    }

    @Test
    public void testValidMonthLowerRange() {
        final Configuration input = this.createConfigurationInstance(1, LongTermIntervalType.MONTHS);

        assertThat(this.validator.isValid(input, null)).isTrue();
    }

    @Test
    public void testValidMonthUpperRange() {
        final Configuration input = this.createConfigurationInstance(12, LongTermIntervalType.MONTHS);

        assertThat(this.validator.isValid(input, null)).isTrue();
    }

    @Test
    public void testInvalidMonthLowerRange() {
        final Configuration input = this.createConfigurationInstance(0, LongTermIntervalType.MONTHS);

        assertThat(this.validator.isValid(input, null)).isFalse();
    }

    @Test
    public void testInvalidMonthUpperRange() {
        final Configuration input = this.createConfigurationInstance(13, LongTermIntervalType.MONTHS);

        assertThat(this.validator.isValid(input, null)).isFalse();
    }
}
