/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFirmwareVersionResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    protected GetFirmwareVersionResponseMessageProcessor() {
        super(MessageType.GET_FIRMWARE_VERSION);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        final Object dataObject = responseMessage.getDataObject();
        return dataObject instanceof ArrayList;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {

        if (responseMessage.getDataObject() instanceof ArrayList) {
            @SuppressWarnings("unchecked")
            final List<FirmwareVersionDto> firmwareVersionList = (List<FirmwareVersionDto>) responseMessage
                    .getDataObject();

            this.configurationService.handleGetFirmwareVersionResponse(deviceMessageMetadata,
                    responseMessage.getResult(), osgpException, firmwareVersionList);
        } else {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            "DataObject for response message should be of type ArrayList"));
        }
    }
}
