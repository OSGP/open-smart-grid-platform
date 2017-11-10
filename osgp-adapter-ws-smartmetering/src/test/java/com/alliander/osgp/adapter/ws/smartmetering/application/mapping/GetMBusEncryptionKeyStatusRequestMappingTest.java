/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetMBusEncryptionKeyStatusRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMBusEncryptionKeyStatusRequestData;

public class GetMBusEncryptionKeyStatusRequestMappingTest {

    private static final String MAPPED_OBJECT_NULL_MESSAGE = "Mapped object should not be null.";
    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped field should have the same value.";

    private static final String M_BUS_DEVICE_IDENTIFICATION = "TestMBusDevice";

    private final ConfigurationMapper mapper = new ConfigurationMapper();

    @Test
    public void shouldConvertGetMBusEncryptionKeyStatusDataRequest() {
        final GetMBusEncryptionKeyStatusRequest source = this.makeRequest();
        final GetMBusEncryptionKeyStatusRequestData result = this.mapper.map(source,
                GetMBusEncryptionKeyStatusRequestData.class);
        assertNotNull(MAPPED_OBJECT_NULL_MESSAGE, result);
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, source.getMBusDeviceIdentification(),
                result.getMBusDeviceIdentification());

    }

    private GetMBusEncryptionKeyStatusRequest makeRequest() {

        final GetMBusEncryptionKeyStatusRequest result = new GetMBusEncryptionKeyStatusRequest();
        result.setMBusDeviceIdentification(M_BUS_DEVICE_IDENTIFICATION);
        return result;
    }
}
