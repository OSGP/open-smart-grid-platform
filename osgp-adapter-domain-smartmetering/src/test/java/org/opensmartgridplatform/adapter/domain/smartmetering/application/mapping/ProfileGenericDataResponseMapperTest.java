/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

public class ProfileGenericDataResponseMapperTest {

    private MonitoringMapper mapper = new MonitoringMapper();

    private final static Class<?>[] EXPECTED_CLASS = new Class<?>[] { String.class, Date.class, BigDecimal.class,
            Long.class };

    @Test
    public void testConvertProfileGenericDataResponseVo() {
        ProfileGenericDataResponseDto responseDto = this.makeResponseDto();
        ProfileGenericDataResponse responseVo = this.mapper.map(responseDto, ProfileGenericDataResponse.class);
        assertNotNull("response object should not be null", responseVo);
        assertEquals("response object should return same number of profilentries", responseVo.getProfileEntries()
                .get(0).getProfileEntryValues().size(), EXPECTED_CLASS.length);
        int i = 0;
        for (ProfileEntryValue profileEntryValueVo : responseVo.getProfileEntries().get(0).getProfileEntryValues()) {
            Class<?> clazz = profileEntryValueVo.getValue().getClass();
            assertEquals("the return class should be of the same type", EXPECTED_CLASS[i++], clazz);
        }
    }

    private ProfileGenericDataResponseDto makeResponseDto() {
        final ObisCodeValuesDto obisCodeValuesDto = new ObisCodeValuesDto((byte) 1, (byte) 1, (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);
        ProfileGenericDataResponseDto result = new ProfileGenericDataResponseDto(obisCodeValuesDto,
                this.makeCaptureObjectDtos(), this.makeProfileEntryDtos());
        return result;
    }

    private List<CaptureObjectDto> makeCaptureObjectDtos() {
        List<CaptureObjectDto> result = new ArrayList<CaptureObjectDto>();
        return result;
    }

    private List<ProfileEntryDto> makeProfileEntryDtos() {
        List<ProfileEntryDto> result = new ArrayList<ProfileEntryDto>();
        result.add(new ProfileEntryDto(this.makeProfileEntryValueDtos()));
        return result;
    }

    private List<ProfileEntryValueDto> makeProfileEntryValueDtos() {
        List<ProfileEntryValueDto> result = new ArrayList<ProfileEntryValueDto>();
        result.add(new ProfileEntryValueDto("Test"));
        result.add(new ProfileEntryValueDto(new Date()));
        result.add(new ProfileEntryValueDto(new BigDecimal(123.45d)));
        result.add(new ProfileEntryValueDto(12345L));
        return result;
    }
}
