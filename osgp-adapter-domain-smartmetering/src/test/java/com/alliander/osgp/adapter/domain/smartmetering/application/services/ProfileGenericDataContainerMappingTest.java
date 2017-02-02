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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseItemDto;

public class ProfileGenericDataContainerMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    // the List is not allowed to be null because of the way the constructor is
    // defined
    @Test(expected = NullPointerException.class)
    public void testWithNullList() {

        final List<ProfileGenericDataResponseItemDto> meterReads = null;
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        new ProfileGenericDataResponseDto(periodType, meterReads);
    }

    // Test if mapping with an empty List succeeds
    @Test
    public void testWithEmptyList() {

        final List<ProfileGenericDataResponseItemDto> meterReads = new ArrayList<>();
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final ProfileGenericDataResponseDto periodicMeterReadsContainerDto = new ProfileGenericDataResponseDto(
                periodType, meterReads);

        final ProfileGenericDataContainer periodicMeterReadContainer = this.monitoringMapper.map(
                periodicMeterReadsContainerDto, ProfileGenericDataContainer.class);

        assertNotNull(periodicMeterReadContainer);

        assertTrue(periodicMeterReadContainer.getItems().isEmpty());
        assertEquals(periodicMeterReadsContainerDto.getPeriodType().name(), periodicMeterReadContainer.getPeriodType()
                .name());
    }

    // Test if mapping with a non-empty List succeeds
    @Test
    public void testWithNonEmptyList() {
        // build test data
        final DlmsMeterValueDto activeEnergyImport = new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitDto.M3);
        final DlmsMeterValueDto activeEnergyExport = new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitDto.M3);

        final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlagSet = new TreeSet<>();
        amrProfileStatusCodeFlagSet.add(AmrProfileStatusCodeFlagDto.CRITICAL_ERROR);
        final AmrProfileStatusCodeDto amrProfileStatusCodeDto = new AmrProfileStatusCodeDto(amrProfileStatusCodeFlagSet);

        final ProfileGenericDataResponseItemDto periodicMeterReadsDto = new ProfileGenericDataResponseItemDto(
                new Date(), activeEnergyImport, activeEnergyExport, amrProfileStatusCodeDto);
        final List<ProfileGenericDataResponseItemDto> meterReads = new ArrayList<ProfileGenericDataResponseItemDto>();
        meterReads.add(periodicMeterReadsDto);

        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;

        final ProfileGenericDataResponseDto periodicMeterReadsContainerDto = new ProfileGenericDataResponseDto(
                periodType, meterReads);
        // actual mapping
        final ProfileGenericDataContainer periodicMeterReadsContainer = this.monitoringMapper.map(
                periodicMeterReadsContainerDto, ProfileGenericDataContainer.class);
        // test mapping
        assertNotNull(periodicMeterReadsContainer);

        assertEquals(periodicMeterReadsContainerDto.getPeriodType().name(), periodicMeterReadsContainer.getPeriodType()
                .name());

        assertEquals(periodicMeterReadsContainerDto.getItems().size(), periodicMeterReadsContainer
                .getItems().size());
        assertEquals(periodicMeterReadsContainerDto.getItems().get(0).getLogTime(),
                periodicMeterReadsContainer.getItems().get(0).getLogTime());

        assertEquals(new BigDecimal("1.0"), periodicMeterReadsContainer.getItems().get(0).getActiveEnergyImport()
                .getValue());
        assertEquals(OsgpUnit.M3, periodicMeterReadsContainer.getItems().get(0).getActiveEnergyImport().getOsgpUnit());
        assertEquals(new BigDecimal("1.0"), periodicMeterReadsContainer.getItems().get(0).getActiveEnergyExport()
                .getValue());
        assertEquals(OsgpUnit.M3, periodicMeterReadsContainer.getItems().get(0).getActiveEnergyExport().getOsgpUnit());

        assertEquals(periodicMeterReadsContainerDto.getItems().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags().size(), periodicMeterReadsContainer.getItems().get(0)
                .getAmrProfileStatusCode().getAmrProfileStatusCodeFlags().size());

        assertTrue(periodicMeterReadsContainer.getItems().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlags().contains(AmrProfileStatusCodeFlag.CRITICAL_ERROR));
    }
}
