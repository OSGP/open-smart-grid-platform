/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.InstallationService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Component
public class CoupleMbusDeviceByChannelResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private InstallationService installationService;

    protected CoupleMbusDeviceByChannelResponseMessageProcessor() {
        super(DeviceFunction.COUPLE_MBUS_DEVICE_BY_CHANNEL);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        return responseMessage.getDataObject() instanceof CoupleMbusDeviceByChannelResponseDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {
        this.installationService.handleCoupleMbusDeviceByChannelResponse(deviceMessageMetadata,
                responseMessage.getResult(), responseMessage.getOsgpException(),
                (CoupleMbusDeviceByChannelResponseDto) responseMessage.getDataObject());
    }
}
