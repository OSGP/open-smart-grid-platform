/**
 * Copyright 2018 Smart Society Services B.V.
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
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Component
public class GetMbusEncryptionKeyStatusByChannelRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    public GetMbusEncryptionKeyStatusByChannelRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL);
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException {

        this.assertRequestObjectType(GetMbusEncryptionKeyStatusByChannelRequestDataDto.class, requestObject);
        final GetMbusEncryptionKeyStatusByChannelRequestDataDto request = (GetMbusEncryptionKeyStatusByChannelRequestDataDto) requestObject;
        return this.configurationService.requestGetMbusEncryptionKeyStatusByChannel(conn, device, request);
    }

}
