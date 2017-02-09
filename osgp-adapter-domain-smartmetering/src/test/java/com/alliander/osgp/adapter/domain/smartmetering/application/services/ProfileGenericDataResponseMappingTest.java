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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryItemDto;
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

    @Test
    public void test4() {
        final CaptureObjectItemVo result = this.mapper.map(this.captureObjectItemDto(), CaptureObjectItemVo.class);
        Assert.assertTrue(result != null && result instanceof CaptureObjectItemVo);
    }

    @Test
    public void test5() {
        final ProfileEntryItemVo result = this.mapper.map(this.profileEntryItemDto(), ProfileEntryItemVo.class);
        Assert.assertTrue(result != null && result instanceof ProfileEntryItemVo);
    }

    @Test
    public void test6() {
        final ObisCodeValues result = this.mapper.map(this.obisCodeDto(), ObisCodeValues.class);
        Assert.assertTrue(result != null && result instanceof ObisCodeValues);
    }

    private ProfileGenericDataResponseDto makeDto() {
        final List<CaptureObjectItemDto> captureObjects = new ArrayList<CaptureObjectItemDto>();
        captureObjects.add(new CaptureObjectItemDto(this.captureObjectDto()));
        final List<ProfileEntryItemDto> profileEntries = this.makeProfileEntryItemDtos();

        ProfileGenericDataResponseDto dto = new ProfileGenericDataResponseDto(this.obisCodeDto(), captureObjects,
                profileEntries);
        return dto;
    }

    private List<ProfileEntryItemDto> makeProfileEntryItemDtos() {
        List<ProfileEntryItemDto> result = new ArrayList<ProfileEntryItemDto>();
        result.add(this.profileEntryItemDto());
        return result;
    }

    private ProfileEntryDto makeProfileEntryDto() {
        ProfileEntryDto result = new ProfileEntryDto("test");
        return result;
    }

    private CaptureObjectItemDto captureObjectItemDto() {
        return new CaptureObjectItemDto(new CaptureObjectDto(10L, this.obisCodeDto(), 2, 0, "kwu"));
    }

    private CaptureObjectDto captureObjectDto() {
        return new CaptureObjectDto(10L, this.obisCodeDto(), 2, 0, "kwu");
    }

    private ProfileEntryItemDto profileEntryItemDto() {
        List<ProfileEntryDto> entriesDto = new ArrayList<ProfileEntryDto>();
        entriesDto.add(this.makeProfileEntryDto());
        return new ProfileEntryItemDto(entriesDto);
    }

    private ProfileEntryDto profileEntryDtoDouble() {
        return new ProfileEntryDto(new BigDecimal(10.5));
    }

    private ProfileEntryDto profileEntryDtoLong() {
        return new ProfileEntryDto(10L);
    }

    private ProfileEntryDto profileEntryDtoDate() {
        return new ProfileEntryDto(new Date());
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
