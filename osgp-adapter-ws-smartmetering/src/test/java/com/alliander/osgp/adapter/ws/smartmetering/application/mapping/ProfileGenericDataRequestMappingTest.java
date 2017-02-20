/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;

public class ProfileGenericDataRequestMappingTest {

    private final MonitoringMapper mapper = new MonitoringMapper();

    @Test
    public void convertProfileGenericDataRequest() {
        final ProfileGenericDataRequest req1 = this.makeRequest();
        final Object obj1 = this.mapper.map(req1,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest.class);
        assertTrue((obj1 != null)
                && (obj1 instanceof com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest));

        final Object obj2 = this.mapper.map(obj1, ProfileGenericDataRequest.class);
        assertTrue((obj2 != null) && (obj2 instanceof ProfileGenericDataRequest));
        final ProfileGenericDataRequest req2 = (ProfileGenericDataRequest) obj2;
        assertTrue(req1.getDeviceIdentification().equals(req2.getDeviceIdentification()));
    }

    private ProfileGenericDataRequest makeRequest() {
        final ProfileGenericDataRequest result = new ProfileGenericDataRequest();
        final ObisCodeValues obiscode = new ObisCodeValues();
        obiscode.setA((short) 1);
        result.setObisCode(obiscode);
        result.setDeviceIdentification("12345");
        return result;
    }
}
