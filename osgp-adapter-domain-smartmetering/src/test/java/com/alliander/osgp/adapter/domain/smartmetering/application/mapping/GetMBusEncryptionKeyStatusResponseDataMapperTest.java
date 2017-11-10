/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMBusEncryptionKeyStatusResponseData;
import com.alliander.osgp.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMBusEncryptionKeyStatusResponseDto;

public class GetMBusEncryptionKeyStatusResponseDataMapperTest {

    private static final String MAPPED_OBJECT_VALUE_MESSAGE = "Mapped object should not be null.";
    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped field should have the same value.";

    private static final String M_BUS_DEVICE_IDENTIFICATION = "TestMbusDevice";
    private static final EncryptionKeyStatusTypeDto ENCRYPTION_KEY_STATUS = EncryptionKeyStatusTypeDto.ENCRYPTION_KEY_IN_USE;

    private ConfigurationMapper mapper = new ConfigurationMapper();

    @Test
    public void shouldConvertDtoToValueObject() {
        final GetMBusEncryptionKeyStatusResponseDto source = this.makeResponse();
        final GetMBusEncryptionKeyStatusResponseData result = this.mapper.map(source,
                GetMBusEncryptionKeyStatusResponseData.class);

        assertNotNull(MAPPED_OBJECT_VALUE_MESSAGE, result);
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, source.getMBusDeviceIdentification(),
                result.getMBusDeviceIdentification());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, source.getEncryptionKeyStatus().getValue(),
                result.getEncryptionKeyStatus().getValue());
    }

    private GetMBusEncryptionKeyStatusResponseDto makeResponse() {
        final GetMBusEncryptionKeyStatusResponseDto response = new GetMBusEncryptionKeyStatusResponseDto(
                M_BUS_DEVICE_IDENTIFICATION, ENCRYPTION_KEY_STATUS);
        return response;
    }
}
