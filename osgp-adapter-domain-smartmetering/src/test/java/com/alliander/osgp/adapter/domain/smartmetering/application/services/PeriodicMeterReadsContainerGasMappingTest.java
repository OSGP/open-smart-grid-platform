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
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;

public class PeriodicMeterReadsContainerGasMappingTest {

    private final MonitoringMapper monitoringMapper = new MonitoringMapper();

    // the List is not allowed to be null because of the way the constructor is
    // defined
    @Test(expected = NullPointerException.class)
    public void testWithNullList() {

        final List<PeriodicMeterReadsGasResponseItemDto> meterReads = null;
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        new PeriodicMeterReadGasResponseDto(periodType, meterReads);
    }

    // Test if mapping with an empty List succeeds
    @Test
    public void testWithEmptyList() {

        final List<PeriodicMeterReadsGasResponseItemDto> meterReads = new ArrayList<>();
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final PeriodicMeterReadGasResponseDto periodicMeterReadsContainerGasDto = new PeriodicMeterReadGasResponseDto(
                periodType, meterReads);

        final PeriodicMeterReadsContainerGas periodicMeterReadContainerGas = this.monitoringMapper.map(
                periodicMeterReadsContainerGasDto, PeriodicMeterReadsContainerGas.class);

        assertNotNull(periodicMeterReadContainerGas);

        assertTrue(periodicMeterReadContainerGas.getPeriodicMeterReadsGas().isEmpty());
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

        final PeriodicMeterReadsGasResponseItemDto periodicMeterReadsGasDto = new PeriodicMeterReadsGasResponseItemDto(
                new Date(), consumption, new Date(), amrProfileStatusCodeDto);
        final List<PeriodicMeterReadsGasResponseItemDto> meterReads = new ArrayList<>();
        meterReads.add(periodicMeterReadsGasDto);

        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final PeriodicMeterReadGasResponseDto periodicMeterReadsContainerDto = new PeriodicMeterReadGasResponseDto(
                periodType, meterReads);
        // actual mapping
        final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas = this.monitoringMapper.map(
                periodicMeterReadsContainerDto, PeriodicMeterReadsContainerGas.class);
        // test mapping
        assertNotNull("Mapping must take place. So the result cannot be null.", periodicMeterReadsContainerGas);

        assertEquals("After the mapping the name of the period must be the same.", periodicMeterReadsContainerDto
                .getPeriodType().name(), periodicMeterReadsContainerGas.getPeriodType().name());

        assertEquals("The number of periodic meter reads before and after the mapping must be equal.",
                periodicMeterReadsContainerDto.getPeriodicMeterReadsGas().size(), periodicMeterReadsContainerGas
                        .getPeriodicMeterReadsGas().size());
        assertEquals("After the mapping the log time of the first entry must be the same.",
                periodicMeterReadsContainerDto.getPeriodicMeterReadsGas().get(0).getLogTime(),
                periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().get(0).getLogTime());
        assertEquals("After the mapping the capture time of the first entry must be the same.",
                periodicMeterReadsContainerDto.getPeriodicMeterReadsGas().get(0).getCaptureTime(),
                periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().get(0).getCaptureTime());

        assertEquals("After the mapping the consumption must be equal.", new BigDecimal("1.0"),
                periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().get(0).getConsumption().getValue());
        assertEquals("After the mapping the osgp unit value must be the same", OsgpUnit.M3,
                periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().get(0).getConsumption().getOsgpUnit());

        assertEquals("After the mapping the size of the arm profile status code flags must be the same.",
                periodicMeterReadsContainerDto.getPeriodicMeterReadsGas().get(0).getAmrProfileStatusCode()
                        .getAmrProfileStatusCodeFlags().size(), periodicMeterReadsContainerGas
                        .getPeriodicMeterReadsGas().get(0).getAmrProfileStatusCode().getAmrProfileStatusCodeFlags()
                        .size());

        assertTrue("After the mapping the amr profile status code flags must contain the CRITICAL_ERROR flag.",
                periodicMeterReadsContainerGas.getPeriodicMeterReadsGas().get(0).getAmrProfileStatusCode()
                        .getAmrProfileStatusCodeFlags().contains(AmrProfileStatusCodeFlag.CRITICAL_ERROR));
    }
}
