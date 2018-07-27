/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

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

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;

public class PeriodicMeterReadContainerMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    // the List is not allowed to be null because of the way the constructor is
    // defined
    @Test(expected = NullPointerException.class)
    public void testWithNullList() {

        final List<PeriodicMeterReadsResponseItemDto> meterReads = null;
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        new PeriodicMeterReadsResponseDto(periodType, meterReads);
    }

    // Test if mapping with an empty List succeeds
    @Test
    public void testWithEmptyList() {

        final List<PeriodicMeterReadsResponseItemDto> meterReads = new ArrayList<>();
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final PeriodicMeterReadsResponseDto periodicMeterReadsContainerDto = new PeriodicMeterReadsResponseDto(
                periodType, meterReads);

        final PeriodicMeterReadsContainer periodicMeterReadContainer = this.monitoringMapper.map(
                periodicMeterReadsContainerDto, PeriodicMeterReadsContainer.class);

        assertNotNull(periodicMeterReadContainer);

        assertTrue(periodicMeterReadContainer.getPeriodicMeterReads().isEmpty());
        assertEquals(periodicMeterReadsContainerDto.getPeriodType().name(), periodicMeterReadContainer.getPeriodType()
                .name());
    }

    // Test if mapping with a non-empty List succeeds
    @Test
    public void testWithNonEmptyList() {
        // build test data
        final DlmsMeterValueDto activeEnergyImport = new DlmsMeterValueDto(new BigDecimal(1.0),
                DlmsUnitTypeDto.M3);
        final DlmsMeterValueDto activeEnergyExport = new DlmsMeterValueDto(new BigDecimal(1.0),
                DlmsUnitTypeDto.M3);

        final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlagSet = new TreeSet<>();
        amrProfileStatusCodeFlagSet.add(AmrProfileStatusCodeFlagDto.CRITICAL_ERROR);
        final AmrProfileStatusCodeDto amrProfileStatusCodeDto = new AmrProfileStatusCodeDto(amrProfileStatusCodeFlagSet);

        final PeriodicMeterReadsResponseItemDto periodicMeterReadsDto = new PeriodicMeterReadsResponseItemDto(
                new Date(), activeEnergyImport, activeEnergyExport, amrProfileStatusCodeDto);
        final List<PeriodicMeterReadsResponseItemDto> meterReads = new ArrayList<PeriodicMeterReadsResponseItemDto>();
        meterReads.add(periodicMeterReadsDto);

        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final PeriodicMeterReadsResponseDto periodicMeterReadsContainerDto = new PeriodicMeterReadsResponseDto(
                periodType, meterReads);
        // actual mapping
        final PeriodicMeterReadsContainer periodicMeterReadsContainer = this.monitoringMapper.map(
                periodicMeterReadsContainerDto, PeriodicMeterReadsContainer.class);
        // test mapping
        assertNotNull(periodicMeterReadsContainer);

        assertEquals(periodicMeterReadsContainerDto.getPeriodType().name(), periodicMeterReadsContainer.getPeriodType()
                .name());

        assertEquals(periodicMeterReadsContainerDto.getPeriodicMeterReads().size(), periodicMeterReadsContainer
                .getPeriodicMeterReads().size());
        assertEquals(periodicMeterReadsContainerDto.getPeriodicMeterReads().get(0).getLogTime(),
                periodicMeterReadsContainer.getPeriodicMeterReads().get(0).getLogTime());

        assertEquals(new BigDecimal("1.0"), periodicMeterReadsContainer.getPeriodicMeterReads().get(0)
                .getActiveEnergyImport().getValue());
        assertEquals(OsgpUnit.M3, periodicMeterReadsContainer.getPeriodicMeterReads().get(0).getActiveEnergyImport()
                .getOsgpUnit());
        assertEquals(new BigDecimal("1.0"), periodicMeterReadsContainer.getPeriodicMeterReads().get(0)
                .getActiveEnergyExport().getValue());
        assertEquals(OsgpUnit.M3, periodicMeterReadsContainer.getPeriodicMeterReads().get(0).getActiveEnergyExport()
                .getOsgpUnit());

        assertEquals(periodicMeterReadsContainerDto.getPeriodicMeterReads().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags().size(), periodicMeterReadsContainer.getPeriodicMeterReads().get(0)
                .getAmrProfileStatusCode().getAmrProfileStatusCodeFlags().size());

        assertTrue(periodicMeterReadsContainer.getPeriodicMeterReads().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags().contains(AmrProfileStatusCodeFlag.CRITICAL_ERROR));
    }
}
