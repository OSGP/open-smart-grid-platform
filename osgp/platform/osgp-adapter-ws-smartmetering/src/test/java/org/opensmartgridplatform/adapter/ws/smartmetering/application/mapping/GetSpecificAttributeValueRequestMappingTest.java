/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class GetSpecificAttributeValueRequestMappingTest {

    private final AdhocMapper mapper = new AdhocMapper();

    @Test
    public void test() {
        final GetSpecificAttributeValueRequest req1 = this.makeRequest();
        final Object obj1 = this.mapper.map(req1,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest.class);
        assertTrue((obj1 != null)
                && (obj1 instanceof org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest));
        final Object obj2 = this.mapper.map(obj1, GetSpecificAttributeValueRequest.class);
        assertTrue((obj2 != null) && (obj2 instanceof GetSpecificAttributeValueRequest));
        final GetSpecificAttributeValueRequest req2 = (GetSpecificAttributeValueRequest) obj2;
        assertTrue(req1.getDeviceIdentification().equals(req2.getDeviceIdentification()));
    }

    private GetSpecificAttributeValueRequest makeRequest() {
        final GetSpecificAttributeValueRequest result = new GetSpecificAttributeValueRequest();
        final ObisCodeValues obiscode = new ObisCodeValues();
        obiscode.setA((short) 1);
        result.setObisCode(obiscode);
        result.setDeviceIdentification("12345");
        return result;
    }

}
