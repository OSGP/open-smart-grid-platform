/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;

public class ProfileGenericDataResponseMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    @Test
    public void test() {
        ProfileGenericDataResponseVo voResponse = this.makeValueObject();
        ProfileGenericDataResponse wsResponse = this.monitoringMapper.map(voResponse, ProfileGenericDataResponse.class);
        Assert.assertTrue(wsResponse != null && wsResponse instanceof ProfileGenericDataResponse);
    }

    private ProfileGenericDataResponseVo makeValueObject() {
        final ObisCodeValues obisCode = new ObisCodeValues((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
        ProfileGenericDataResponseVo result = new ProfileGenericDataResponseVo(obisCode,
                new ArrayList<CaptureObjectVo>(), new ArrayList<ProfileEntryVo>());
        return result;
    }

}
