/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataRequestVo;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;

public class ProfileGenericDataRequestMapperTest {

    private MonitoringMapper mapper = new MonitoringMapper();

    @Test
    public void testConvertProfileGenericDataRequestDto() {
        ProfileGenericDataRequestVo reqData1 = this.makeRequest();
        Object obj = this.mapper.map(reqData1, ProfileGenericDataRequestDto.class);
        assertTrue(obj != null && obj instanceof ProfileGenericDataRequestDto);
    }

    private ProfileGenericDataRequestVo makeRequest() {
        final ObisCodeValues obiscode = new ObisCodeValues((byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7);
        return new ProfileGenericDataRequestVo(obiscode, new Date(), new Date(), "TEST10240000001");
    }

}
