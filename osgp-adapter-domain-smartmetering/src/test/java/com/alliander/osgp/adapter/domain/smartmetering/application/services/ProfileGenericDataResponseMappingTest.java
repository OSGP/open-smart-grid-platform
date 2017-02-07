/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.math.BigDecimal;
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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryDto;
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
        final ProfileEntryVo result = this.mapper.map(this.profileEntryDtoDate(), ProfileEntryVo.class);
        Assert.assertTrue(result != null && result instanceof ProfileEntryVo);
    }

    private ProfileGenericDataResponseDto makeDto() {
        final List<CaptureObjectDto> captureObjects = new ArrayList<CaptureObjectDto>();
        captureObjects.add(this.captureObjectDto());

        final List<ProfileEntryDto> profileEntries = new ArrayList<ProfileEntryDto>();
        profileEntries.add(this.profileEntryDtoDate());
        profileEntries.add(this.profileEntryDtoString());
        profileEntries.add(this.profileEntryDtoLong());
        profileEntries.add(this.profileEntryDtoDouble());

        ProfileGenericDataResponseDto dto = new ProfileGenericDataResponseDto(this.obisCodeDto(), captureObjects,
                profileEntries);
        return dto;
    }

    private CaptureObjectDto captureObjectDto() {
        return new CaptureObjectDto(10L, this.obisCodeDto(), 2, 0);
    }

    private ProfileEntryDto profileEntryDtoDouble() {
        return new ProfileEntryDto(new BigDecimal(10.5));
    }

    private ProfileEntryDto profileEntryDtoLong() {
        return new ProfileEntryDto(10L);
    }

    private ProfileEntryDto profileEntryDtoDate() {
        return new ProfileEntryDto(this.createXMLGregorianCalendar(new Date()));
    }

    private ProfileEntryDto profileEntryDtoString() {
        return new ProfileEntryDto("test");
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
