/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.InstallationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeCoupleMbusDeviceByChannelRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private InstallationService installationService;

    protected DeCoupleMbusDeviceByChannelRequestMessageProcessor() {
        super(MessageType.DE_COUPLE_MBUS_DEVICE_BY_CHANNEL);
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionManager conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException {

        this.assertRequestObjectType(DeCoupleMbusDeviceByChannelRequestDataDto.class, requestObject);

        final DeCoupleMbusDeviceByChannelRequestDataDto requestDto = (DeCoupleMbusDeviceByChannelRequestDataDto) requestObject;
        return this.installationService.deCoupleMbusDeviceByChannel(conn, device, requestDto);
    }
}
