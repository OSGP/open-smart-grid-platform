/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActiveEnergyValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActiveEnergyValuesDto;
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
        final AmrProfileStatusCodeDto amrProfileStatusCodeDto = new AmrProfileStatusCodeDto(
                amrProfileStatusCodeFlagSet);

        final PeriodicMeterReadsResponseItemDto periodicMeterReadsDto = new PeriodicMeterReadsResponseItemDto(
                new Date(), activeEnergyImport, activeEnergyExport, amrProfileStatusCodeDto);
        final List<PeriodicMeterReadsResponseItemDto> meterReads = new ArrayList<>();
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

    @Test
    public void mapsPeriodicMeterReadsResponseItemDto() {
        final Date logTime = new Date();
        final ActiveEnergyValuesDto valuesDto = new ActiveEnergyValuesDto(
                new DlmsMeterValueDto(new BigDecimal("12.34"), DlmsUnitTypeDto.M3),
                new DlmsMeterValueDto(new BigDecimal("12.35"), DlmsUnitTypeDto.M3),
                new DlmsMeterValueDto(new BigDecimal("12.36"), DlmsUnitTypeDto.M3),
                new DlmsMeterValueDto(new BigDecimal("12.37"), DlmsUnitTypeDto.M3),
                new DlmsMeterValueDto(new BigDecimal("12.38"), DlmsUnitTypeDto.M3),
                new DlmsMeterValueDto(new BigDecimal("12.39"), DlmsUnitTypeDto.M3));
        final AmrProfileStatusCodeDto amrProfileStatusCodeDto = new AmrProfileStatusCodeDto(new HashSet<>(asList(
                AmrProfileStatusCodeFlagDto.CRITICAL_ERROR, AmrProfileStatusCodeFlagDto.CLOCK_ADJUSTED)));
        final PeriodicMeterReadsResponseItemDto source = new PeriodicMeterReadsResponseItemDto(logTime, valuesDto,
                amrProfileStatusCodeDto);

        final PeriodicMeterReads readsResult = this.monitoringMapper.map(source, PeriodicMeterReads.class);

        final ActiveEnergyValues expectedValues = new ActiveEnergyValues(
                new OsgpMeterValue(new BigDecimal("12.340"), OsgpUnit.M3),
                new OsgpMeterValue(new BigDecimal("12.350"), OsgpUnit.M3),
                new OsgpMeterValue(new BigDecimal("12.360"), OsgpUnit.M3),
                new OsgpMeterValue(new BigDecimal("12.370"), OsgpUnit.M3),
                new OsgpMeterValue(new BigDecimal("12.380"), OsgpUnit.M3),
                new OsgpMeterValue(new BigDecimal("12.390"), OsgpUnit.M3));
        final AmrProfileStatusCode amrProfileStatusCode = new AmrProfileStatusCode(new HashSet<>(asList(
                AmrProfileStatusCodeFlag.CRITICAL_ERROR, AmrProfileStatusCodeFlag.CLOCK_ADJUSTED)));
        final PeriodicMeterReads expectedReads = new PeriodicMeterReads(logTime, expectedValues, amrProfileStatusCode);
        assertThat(readsResult).isEqualToComparingFieldByFieldRecursively(expectedReads);
    }
}
