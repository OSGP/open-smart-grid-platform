/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.osgp.adapter.protocol.dlms.application.services.InstallationService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

/**
 * Class for processing add meter request messages
 */
@Component
public class AddMeterRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private InstallationService installationService;

    public AddMeterRequestMessageProcessor() {
        super(DeviceRequestMessageType.ADD_METER);
    }

    @Override
    protected boolean usesDeviceConnection() {
        return false;
    }

    @Override
    protected Serializable handleMessage(final DlmsDevice device, final Serializable requestObject)
            throws OsgpException, ProtocolAdapterException {
        this.assertRequestObjectType(SmartMeteringDeviceDto.class, requestObject);

        final SmartMeteringDeviceDto smartMeteringDevice = (SmartMeteringDeviceDto) requestObject;
        this.installationService.addMeter(smartMeteringDevice);

        // No return object.
        return null;
    }
}
