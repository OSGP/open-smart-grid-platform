/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;

@Component
public class CoupleMbusDeviceResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private InstallationService installationService;

    protected CoupleMbusDeviceResponseMessageProcessor() {
        super(DeviceFunction.COUPLE_MBUS_DEVICE);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        return responseMessage.getDataObject() instanceof MbusChannelElementsResponseDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {

        this.installationService.handleCoupleMbusDeviceResponse(deviceMessageMetadata, responseMessage.getResult(),
                responseMessage.getOsgpException(), (MbusChannelElementsResponseDto) responseMessage.getDataObject());
    }
}
