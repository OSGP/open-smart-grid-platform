/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;

public class ProfileGenericDataRequestMapperTest {

    private final MonitoringMapper mapper = new MonitoringMapper();

    private static final Date DATE = new Date();
    private static final String DEVICE_NAME = "TEST10240000001";
    private static final String MAPPED_VALUE_MESSAGE = "mapped values should be identical.";

    @Test
    public void testConvertProfileGenericDataRequestDto() {
        final ProfileGenericDataRequest profileGenericDateRequest = this.makeRequest();
        final Object result = this.mapper.map(profileGenericDateRequest, ProfileGenericDataRequestDto.class);
        assertThat(result).withFailMessage("mapping ProfileGenericDataRequest should not return null").isNotNull();

        assertThat(result).withFailMessage("mapping ProfileGenericDataRequest should return correct type")
                .isOfAnyClassIn(ProfileGenericDataRequestDto.class);

        final ProfileGenericDataRequestDto profileGenericDataRequestDto = (ProfileGenericDataRequestDto) result;
        assertThat(profileGenericDataRequestDto.getBeginDate()).withFailMessage(MAPPED_VALUE_MESSAGE).isEqualTo(DATE);
        assertThat(profileGenericDataRequestDto.getEndDate()).withFailMessage(MAPPED_VALUE_MESSAGE).isEqualTo(DATE);
        assertThat(profileGenericDataRequestDto.getDeviceIdentification()).withFailMessage(MAPPED_VALUE_MESSAGE)
                .isEqualTo(DEVICE_NAME);
        final ObisCodeValuesDto obisCodeValuesDto = profileGenericDataRequestDto.getObisCode();
        assertThat(profileGenericDataRequestDto.getObisCode().getA()).withFailMessage(MAPPED_VALUE_MESSAGE)
                .isEqualTo(obisCodeValuesDto.getA());
    }

    private ProfileGenericDataRequest makeRequest() {
        final ObisCodeValues obiscode = new ObisCodeValues((byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7);
        return new ProfileGenericDataRequest(obiscode, DATE, DATE, DEVICE_NAME);
    }

}
