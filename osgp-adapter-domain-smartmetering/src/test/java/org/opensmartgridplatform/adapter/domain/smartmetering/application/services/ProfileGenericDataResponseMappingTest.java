/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

public class ProfileGenericDataResponseMappingTest {

    private MonitoringMapper mapper = new MonitoringMapper();

    @Test
    public void testConvertProfileGenericDataResponseDto() {
        final ProfileGenericDataResponseDto dto = this.makeProfileGenericDataResponseDto();
        final ProfileGenericDataResponse result = this.mapper.map(dto, ProfileGenericDataResponse.class);
        assertNotNull("mapping ProfileGenericDataResponse should not return null", result);
        assertThat("mapping ProfileGenericDataResponse should return correct type", result,
                instanceOf(ProfileGenericDataResponse.class));
    }

    @Test
    public void testConvertcaptureObjectDto() {
        final CaptureObject result = this.mapper.map(this.captureObjectDto(), CaptureObject.class);
        assertNotNull("mapping CaptureObjectDto should not return null", result);
        assertThat("mapping CaptureObjectDto should return correct type", result, instanceOf(CaptureObject.class));
    }

    @Test
    public void testConvertProfileEntryValueDto() {
        final ProfileEntryValue result = this.mapper.map(this.profileEntryDtoDate(), ProfileEntryValue.class);
        assertNotNull("mapping ProfileEntryValueDto should not return null", result);
        assertThat("mapping ProfileEntryValueDto should return correct type", result,
                instanceOf(ProfileEntryValue.class));
    }

    @Test
    public void testConvertProfileEntryDto() {
        final ProfileEntry result = this.mapper.map(this.profileEntryDto(), ProfileEntry.class);
        assertNotNull("mapping ProfileEntryDto should not return null", result);
        assertThat("mapping ProfileEntryDto should return correct type", result, instanceOf(ProfileEntry.class));
    }

    private ProfileGenericDataResponseDto makeProfileGenericDataResponseDto() {
        ProfileGenericDataResponseDto dto = new ProfileGenericDataResponseDto(this.obisCodeDto(),
                this.makeCaptureObjectsDto(), this.makeProfileEntryDtoList());
        return dto;
    }

    private List<CaptureObjectDto> makeCaptureObjectsDto() {
        final List<CaptureObjectDto> captureObjects = new ArrayList<CaptureObjectDto>();
        captureObjects.add(this.captureObjectDto());
        return captureObjects;
    }

    private List<ProfileEntryDto> makeProfileEntryDtoList() {
        final List<ProfileEntryDto> result = new ArrayList<ProfileEntryDto>();
        result.add(this.profileEntryDto());
        return result;
    }

    private ProfileEntryValueDto makeProfileEntryValueDto() {
        ProfileEntryValueDto result = new ProfileEntryValueDto("test");
        return result;
    }

    private CaptureObjectDto captureObjectDto() {
        return new CaptureObjectDto(10L, "0.0.1.0.0.255", 2, 0, OsgpUnit.KWH.name());
    }

    private ProfileEntryDto profileEntryDto() {
        List<ProfileEntryValueDto> entriesDto = new ArrayList<ProfileEntryValueDto>();
        entriesDto.add(this.makeProfileEntryValueDto());
        return new ProfileEntryDto(entriesDto);
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
