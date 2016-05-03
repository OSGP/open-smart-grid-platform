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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasDto;

public class PeriodicMeterReadsContainerGasMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    // the List is not allowed to be null because of the way the constructor is
    // defined
    @Test(expected = NullPointerException.class)
    public void testWithNullList() {

        final List<PeriodicMeterReadsGasDto> meterReads = null;
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        new PeriodicMeterReadsContainerGasDto(periodType, meterReads);
    }

    // Test if mapping with an empty List succeeds
    @Test
    public void testWithEmptyList() {

        final List<PeriodicMeterReadsGasDto> meterReads = new ArrayList<>();
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final PeriodicMeterReadsContainerGasDto periodicMeterReadsContainerGasDto = new PeriodicMeterReadsContainerGasDto(
                periodType, meterReads);

        final PeriodicMeterReadsContainerGas periodicMeterReadContainerGas = this.monitoringMapper.map(
                periodicMeterReadsContainerGasDto, PeriodicMeterReadsContainerGas.class);

        assertNotNull(periodicMeterReadContainerGas);

        assertTrue(periodicMeterReadContainerGas.getMeterReadsGas().isEmpty());
        assertEquals(periodicMeterReadsContainerGasDto.getPeriodType().name(), periodicMeterReadContainerGas
                .getPeriodType().name());
    }

    // Test if mapping with a non-empty List succeeds
    @Test
    public void testWithNonEmptyList() {
        // build test data
        final DlmsMeterValueDto consumption = new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitDto.M3);

        final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlagSet = new TreeSet<>();
        amrProfileStatusCodeFlagSet.add(AmrProfileStatusCodeFlagDto.CRITICAL_ERROR);
        final AmrProfileStatusCodeDto amrProfileStatusCodeDto = new AmrProfileStatusCodeDto(amrProfileStatusCodeFlagSet);

        final PeriodicMeterReadsGasDto periodicMeterReadsGasDto = new PeriodicMeterReadsGasDto(new Date(), consumption,
                new Date(), amrProfileStatusCodeDto);
        final List<PeriodicMeterReadsGasDto> meterReads = new ArrayList<>();
        meterReads.add(periodicMeterReadsGasDto);

        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final PeriodicMeterReadsContainerGasDto periodicMeterReadsContainerDto = new PeriodicMeterReadsContainerGasDto(
                periodType, meterReads);
        // actual mapping
        final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas = this.monitoringMapper.map(
                periodicMeterReadsContainerDto, PeriodicMeterReadsContainerGas.class);
        // test mapping
        assertNotNull(periodicMeterReadsContainerGas);

        assertEquals(periodicMeterReadsContainerDto.getPeriodType().name(), periodicMeterReadsContainerGas
                .getPeriodType().name());

        assertEquals(periodicMeterReadsContainerDto.getMeterReadsGas().size(), periodicMeterReadsContainerGas
                .getMeterReadsGas().size());
        assertEquals(periodicMeterReadsContainerDto.getMeterReadsGas().get(0).getLogTime(),
                periodicMeterReadsContainerGas.getMeterReadsGas().get(0).getLogTime());
        assertEquals(periodicMeterReadsContainerDto.getMeterReadsGas().get(0).getCaptureTime(),
                periodicMeterReadsContainerGas.getMeterReadsGas().get(0).getCaptureTime());

        assertEquals(new BigDecimal("1.0"), periodicMeterReadsContainerGas.getMeterReadsGas().get(0).getConsumption()
                .getValue());
        assertEquals(OsgpUnit.M3, periodicMeterReadsContainerGas.getMeterReadsGas().get(0).getConsumption()
                .getOsgpUnit());

        assertEquals(periodicMeterReadsContainerDto.getMeterReadsGas().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags().size(), periodicMeterReadsContainerGas.getMeterReadsGas().get(0)
                .getAmrProfileStatusCode().getAmrProfileStatusCodeFlags().size());

        assertTrue(periodicMeterReadsContainerGas.getMeterReadsGas().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags().contains(AmrProfileStatusCodeFlag.CRITICAL_ERROR));
    }
}
