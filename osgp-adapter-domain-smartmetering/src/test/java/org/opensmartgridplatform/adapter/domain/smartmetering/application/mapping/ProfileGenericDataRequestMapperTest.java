/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;

public class ProfileGenericDataRequestMapperTest {

    private MonitoringMapper mapper = new MonitoringMapper();

    private static final Date DATE = new Date();
    private static final String DEVICE_NAME = "TEST10240000001";

    @Test
    public void testConvertProfileGenericDataRequestDto() {
        ProfileGenericDataRequest profileGenericDateRequest = this.makeRequest();
        Object result = this.mapper.map(profileGenericDateRequest, ProfileGenericDataRequestDto.class);
        assertNotNull("mapping ProfileGenericDataRequest should not return null", result);
        assertThat("mapping ProfileGenericDataRequest should return correct type", result,
                instanceOf(ProfileGenericDataRequestDto.class));

        final ProfileGenericDataRequestDto profileGenericDataRequestDto = (ProfileGenericDataRequestDto) result;
        assertEquals("mapped values should be identical", profileGenericDataRequestDto.getBeginDate(), DATE);
        assertEquals("mapped values should be identical", profileGenericDataRequestDto.getEndDate(), DATE);
        assertEquals("mapped values should be identical", profileGenericDataRequestDto.getDeviceIdentification(),
                DEVICE_NAME);
        final ObisCodeValuesDto obisCodeValuesDto = profileGenericDataRequestDto.getObisCode();
        assertEquals("mapped values should be identical", obisCodeValuesDto.getA(), profileGenericDateRequest.getObisCode().getA());
    }

    private ProfileGenericDataRequest makeRequest() {
        final ObisCodeValues obiscode = new ObisCodeValues((byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7);
        return new ProfileGenericDataRequest(obiscode, DATE, DATE, DEVICE_NAME);
    }

}
