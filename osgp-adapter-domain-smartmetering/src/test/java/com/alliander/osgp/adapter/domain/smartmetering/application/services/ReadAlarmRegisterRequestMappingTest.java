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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;

public class ReadAlarmRegisterRequestMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    @Test
    public void testReadAlarmRegisterRequestMappingTest() {

        final String deviceId = "device1";
        final ReadAlarmRegisterRequest request = new ReadAlarmRegisterRequest(deviceId);
        final ReadAlarmRegisterRequestDto requestDto = this.monitoringMapper.map(request,
                ReadAlarmRegisterRequestDto.class);

        assertNotNull(requestDto);
        assertEquals(deviceId, requestDto.getDeviceIdentification());
    }

    @Test
    public void testWithNullString() {

        final String deviceId = null;
        final ReadAlarmRegisterRequest request = new ReadAlarmRegisterRequest(deviceId);
        final ReadAlarmRegisterRequestDto requestDto = this.monitoringMapper.map(request,
                ReadAlarmRegisterRequestDto.class);

        assertNotNull(requestDto);
        assertNull(requestDto.getDeviceIdentification());
    }

}
