/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.InstallationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RequestWithMetadata;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeCoupleMbusDeviceRequestMessageProcessor extends DeviceRequestMessageProcessor<DeCoupleMbusDeviceDto> {

    @Autowired
    private InstallationService installationService;

    protected DeCoupleMbusDeviceRequestMessageProcessor() {
        super(MessageType.DE_COUPLE_MBUS_DEVICE);
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionManager conn, final DlmsDevice device,
            final RequestWithMetadata<DeCoupleMbusDeviceDto> request) throws OsgpException {
        return this.installationService.deCoupleMbusDevice(conn, device, request.getRequestObject());
    }
}
