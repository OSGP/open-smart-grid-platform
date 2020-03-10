/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

public class PeriodicMeterReadsQueryMappingTest {

    private final MonitoringMapper monitoringMapper = new MonitoringMapper();

    // A beginDate may never be null.
    @Test
    public void testWithNullBeginDate() {
        final PeriodType periodType = PeriodType.DAILY;
        final Date beginDate = null;
        final Date endDate = new Date();
        final boolean mbusDevice = false;

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
            new PeriodicMeterReadsQuery(periodType, beginDate, endDate, mbusDevice);
        });
    }

    // An endDate may never be null.
    @Test
    public void testWithNullEndDate() {

        final PeriodType periodType = PeriodType.DAILY;
        final Date beginDate = new Date();
        final Date endDate = null;
        final boolean mbusDevice = false;

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
            new PeriodicMeterReadsQuery(periodType, beginDate, endDate, mbusDevice);
        });
    }

    // Test if mapping a PeriodicMeterReadsQuery succeeds if both beginDate and
    // endDate are non-null.
    @Test
    public void TestMapping() {
        // build test data
        final PeriodType periodType = PeriodType.DAILY;
        final Date beginDate = new Date();
        final Date endDate = new Date();
        final boolean mbusDevice = false;

        final PeriodicMeterReadsQuery periodicMeterReadsQuery = new PeriodicMeterReadsQuery(periodType, beginDate,
                endDate, mbusDevice);
        // actual mapping
        final PeriodicMeterReadsRequestDto periodicMeterReadsQueryDto = this.monitoringMapper
                .map(periodicMeterReadsQuery, PeriodicMeterReadsRequestDto.class);
        // test mapping
        assertThat(periodicMeterReadsQueryDto).isNotNull();

        assertThat(periodicMeterReadsQueryDto.getPeriodType().name())
                .isEqualTo(periodicMeterReadsQuery.getPeriodType().name());
        assertThat(periodicMeterReadsQueryDto.getBeginDate()).isEqualTo(periodicMeterReadsQuery.getBeginDate());
        assertThat(periodicMeterReadsQueryDto.getEndDate()).isEqualTo(periodicMeterReadsQuery.getEndDate());
        assertThat(periodicMeterReadsQueryDto.isMbusQuery()).isEqualTo(periodicMeterReadsQuery.isMbusDevice());
    }

}
