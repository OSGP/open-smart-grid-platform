/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.osgp.adapter.protocol.dlms.application.services.ConfigurationService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

/**
 * Class for processing Set M-Bus User Key By Channel request messages
 */
@Component
public class SetMbusUserKeyByChannelRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    public SetMbusUserKeyByChannelRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_MBUS_USER_KEY_BY_CHANNEL);
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException {
        this.assertRequestObjectType(SetMbusUserKeyByChannelRequestDataDto.class, requestObject);

        return this.configurationService.setMbusUserKeyByChannel(conn, device,
                (SetMbusUserKeyByChannelRequestDataDto) requestObject);
    }
}
