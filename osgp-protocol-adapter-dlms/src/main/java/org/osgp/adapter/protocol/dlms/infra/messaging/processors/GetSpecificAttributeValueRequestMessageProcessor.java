/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.osgp.adapter.protocol.dlms.application.services.AdhocService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;

@Component
public class GetSpecificAttributeValueRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private AdhocService adhocService;

    protected GetSpecificAttributeValueRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_SPECIFIC_ATTRIBUTE_VALUE);
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable requestObject) throws ProtocolAdapterException {

        this.assertRequestObjectType(SpecificAttributeValueRequestDto.class, requestObject);

        final SpecificAttributeValueRequestDto specificConfigurationObjectRequestDataDto = (SpecificAttributeValueRequestDto) requestObject;

        return this.adhocService.getSpecificAttributeValue(conn, device, specificConfigurationObjectRequestDataDto);
    }
}
