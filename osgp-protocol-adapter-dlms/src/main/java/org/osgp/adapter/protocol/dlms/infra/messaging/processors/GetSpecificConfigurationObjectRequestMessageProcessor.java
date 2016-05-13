/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.application.services.AdhocService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificConfigurationObjectRequestDto;

@Component
public class GetSpecificConfigurationObjectRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private AdhocService adhocService;

    protected GetSpecificConfigurationObjectRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_SPECIFIC_CONFIGURATION_OBJECT);
    }

    @Override
    protected Serializable handleMessage(final ClientConnection conn, final DlmsDevice device,
            final Serializable requestObject) throws ProtocolAdapterException {
        final SpecificConfigurationObjectRequestDto specificConfigurationObjectRequestDataDto = (SpecificConfigurationObjectRequestDto) requestObject;

        return this.adhocService
                .getSpecificConfigurationObject(conn, device, specificConfigurationObjectRequestDataDto);
    }
}
