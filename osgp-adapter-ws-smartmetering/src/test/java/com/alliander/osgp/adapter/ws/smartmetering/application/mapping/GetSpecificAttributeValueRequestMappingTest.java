/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class GetSpecificAttributeValueRequestMappingTest {

    private final AdhocMapper mapper = new AdhocMapper();

    @Test
    public void test() {
        final SpecificAttributeValueRequest req1 = this.makeRequest();
        final Object obj1 = this.mapper.map(req1,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest.class);
        assertTrue((obj1 != null)
                && (obj1 instanceof com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest));
        final Object obj2 = this.mapper.map(obj1, SpecificAttributeValueRequest.class);
        assertTrue((obj2 != null) && (obj2 instanceof SpecificAttributeValueRequest));
        final SpecificAttributeValueRequest req2 = (SpecificAttributeValueRequest) obj2;
        assertTrue(req1.getDeviceIdentification().equals(req2.getDeviceIdentification()));
    }

    private SpecificAttributeValueRequest makeRequest() {
        final SpecificAttributeValueRequest result = new SpecificAttributeValueRequest();
        final ObisCodeValues obiscode = new ObisCodeValues();
        obiscode.setA((short) 1);
        result.setObisCode(obiscode);
        result.setDeviceIdentification("12345");
        return result;
    }

}
