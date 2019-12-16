/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UpdateFirmwareResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    protected UpdateFirmwareResponseMessageProcessor(
            WebServiceResponseMessageSender responseMessageSender,
            @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap") MessageProcessorMap messageProcessorMap) {
        super(responseMessageSender, messageProcessorMap, MessageType.UPDATE_FIRMWARE,
                ComponentType.DOMAIN_SMART_METERING);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        return true;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {

        if (responseMessage.getDataObject() instanceof UpdateFirmwareResponseDto) {
            final UpdateFirmwareResponseDto updateFirmwareResponse = (UpdateFirmwareResponseDto) responseMessage
                    .getDataObject();

            this.configurationService.handleUpdateFirmwareResponse(deviceMessageMetadata, responseMessage.getResult(),
                    osgpException, updateFirmwareResponse);
        } else {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.DOMAIN_SMART_METERING,
                    new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            "DataObject for response message should be of type UpdateFirmwareResponseDto"));
        }
    }
}
