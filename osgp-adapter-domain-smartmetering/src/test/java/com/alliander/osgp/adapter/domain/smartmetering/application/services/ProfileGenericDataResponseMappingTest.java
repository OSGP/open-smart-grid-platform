/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntriesVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValueVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntriesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

public class ProfileGenericDataResponseMappingTest {

    private MonitoringMapper mapper = new MonitoringMapper();

    @Test
    public void test() {
        final ProfileGenericDataResponseDto dto = this.makeDto();
        final ProfileGenericDataResponseVo result = this.mapper.map(dto, ProfileGenericDataResponseVo.class);
        Assert.assertTrue(result != null && result instanceof ProfileGenericDataResponseVo);
    }

    @Test
    public void test2() {
        final CaptureObjectVo result = this.mapper.map(this.captureObjectDto(), CaptureObjectVo.class);
        Assert.assertTrue(result != null && result instanceof CaptureObjectVo);
    }

    @Test
    public void test3() {
        final ProfileEntryValueVo result = this.mapper.map(this.profileEntryDtoDate(), ProfileEntryValueVo.class);
        Assert.assertTrue(result != null && result instanceof ProfileEntryValueVo);
    }

    @Test
    public void test4() {
        final ProfileEntriesVo result = this.mapper.map(this.profileEntriesDto(), ProfileEntriesVo.class);
        Assert.assertTrue(result != null && result instanceof ProfileEntriesVo);
    }

    private ProfileGenericDataResponseDto makeDto() {
        final List<CaptureObjectDto> captureObjects = new ArrayList<CaptureObjectDto>();
        captureObjects.add(this.captureObjectDto());
        final List<ProfileEntriesDto> profileEntries = this.makeProfileEntryItemDtos();

        ProfileGenericDataResponseDto dto = new ProfileGenericDataResponseDto(this.obisCodeDto(), captureObjects,
                profileEntries);
        return dto;
    }

    private List<ProfileEntriesDto> makeProfileEntryItemDtos() {
        List<ProfileEntriesDto> result = new ArrayList<ProfileEntriesDto>();
        result.add(this.profileEntriesDto());
        return result;
    }

    private ProfileEntryValueDto makeProfileEntryDto() {
        ProfileEntryValueDto result = new ProfileEntryValueDto("test");
        return result;
    }

    private CaptureObjectDto captureObjectDto() {
        return new CaptureObjectDto(10L, "0.0.1.0.0.255", 2, 0, OsgpUnit.KWH.name());
    }

    private ProfileEntriesDto profileEntriesDto() {
        List<ProfileEntryValueDto> entriesDto = new ArrayList<ProfileEntryValueDto>();
        entriesDto.add(this.makeProfileEntryDto());
        return new ProfileEntriesDto(entriesDto);
    }

    private ProfileEntryValueDto profileEntryDtoDate() {
        return new ProfileEntryValueDto(new Date());
    }

    private ObisCodeValuesDto obisCodeDto() {
        return new ObisCodeValuesDto((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
    }

    XMLGregorianCalendar createXMLGregorianCalendar(final Date date) {
        try {
            final GregorianCalendar gregCal = new GregorianCalendar();
            gregCal.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
