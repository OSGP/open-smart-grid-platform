/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

public class PeriodicMeterReadsQueryMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    // A beginDate may never be null.
    @Test(expected = NullPointerException.class)
    public void testWithNullBeginDate() {

        final PeriodType periodType = PeriodType.DAILY;
        final Date beginDate = null;
        final Date endDate = new Date();
        final boolean mbusDevice = false;

        new PeriodicMeterReadsQuery(periodType, beginDate, endDate, mbusDevice);
    }

    // An endDate may never be null.
    @Test(expected = NullPointerException.class)
    public void testWithNullEndDate() {

        final PeriodType periodType = PeriodType.DAILY;
        final Date beginDate = new Date();
        final Date endDate = null;
        final boolean mbusDevice = false;

        new PeriodicMeterReadsQuery(periodType, beginDate, endDate, mbusDevice);
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
        final PeriodicMeterReadsRequestDto periodicMeterReadsQueryDto = this.monitoringMapper.map(
                periodicMeterReadsQuery, PeriodicMeterReadsRequestDto.class);
        // test mapping
        assertNotNull(periodicMeterReadsQueryDto);
        assertEquals(periodicMeterReadsQuery.getPeriodType().name(), periodicMeterReadsQueryDto.getPeriodType().name());
        assertEquals(periodicMeterReadsQuery.getBeginDate(), periodicMeterReadsQueryDto.getBeginDate());
        assertEquals(periodicMeterReadsQuery.getEndDate(), periodicMeterReadsQueryDto.getEndDate());
        assertEquals(periodicMeterReadsQuery.isMbusDevice(), periodicMeterReadsQueryDto.isMbusQuery());
    }

}
