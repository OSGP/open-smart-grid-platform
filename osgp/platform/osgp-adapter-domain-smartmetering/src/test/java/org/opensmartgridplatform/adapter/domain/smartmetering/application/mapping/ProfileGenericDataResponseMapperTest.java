/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

public class ProfileGenericDataResponseMapperTest {

    private final MonitoringMapper mapper = new MonitoringMapper();

    private final static Class<?>[] EXPECTED_CLASS = new Class<?>[] { String.class, Date.class, BigDecimal.class,
            Long.class };

    @Test
    public void testConvertProfileGenericDataResponseVo() {
        final ProfileGenericDataResponseDto responseDto = this.makeResponseDto();
        final ProfileGenericDataResponse responseVo = this.mapper.map(responseDto, ProfileGenericDataResponse.class);
        assertThat(responseVo).withFailMessage("response object should not be null").isNotNull();

        assertThat(responseVo.getProfileEntries().get(0).getProfileEntryValues().size())
                .withFailMessage("response object should return same number of profilentries")
                .isEqualTo(EXPECTED_CLASS.length);
        int i = 0;
        for (final ProfileEntryValue profileEntryValueVo : responseVo.getProfileEntries()
                .get(0)
                .getProfileEntryValues()) {
            final Class<?> clazz = profileEntryValueVo.getValue().getClass();
            assertThat(clazz).withFailMessage("the return class should be of the same type")
                    .isEqualTo(EXPECTED_CLASS[i++]);
        }
    }

    private ProfileGenericDataResponseDto makeResponseDto() {
        final ObisCodeValuesDto obisCodeValuesDto = new ObisCodeValuesDto((byte) 1, (byte) 1, (byte) 1, (byte) 1,
                (byte) 1, (byte) 1);
        final ProfileGenericDataResponseDto result = new ProfileGenericDataResponseDto(obisCodeValuesDto,
                this.makeCaptureObjectDtos(), this.makeProfileEntryDtos());
        return result;
    }

    private List<CaptureObjectDto> makeCaptureObjectDtos() {
        final List<CaptureObjectDto> result = new ArrayList<>();
        return result;
    }

    private List<ProfileEntryDto> makeProfileEntryDtos() {
        final List<ProfileEntryDto> result = new ArrayList<>();
        result.add(new ProfileEntryDto(this.makeProfileEntryValueDtos()));
        return result;
    }

    private List<ProfileEntryValueDto> makeProfileEntryValueDtos() {
        final List<ProfileEntryValueDto> result = new ArrayList<>();
        result.add(new ProfileEntryValueDto("Test"));
        result.add(new ProfileEntryValueDto(new Date()));
        result.add(new ProfileEntryValueDto(new BigDecimal(123.45d)));
        result.add(new ProfileEntryValueDto(12345L));
        return result;
    }
}
