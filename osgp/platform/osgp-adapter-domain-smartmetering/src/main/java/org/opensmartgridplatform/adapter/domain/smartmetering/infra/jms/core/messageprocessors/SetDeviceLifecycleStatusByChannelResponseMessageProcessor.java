/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ManagementService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SetDeviceLifecycleStatusByChannelResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private ManagementService managementService;

    @Autowired
    protected SetDeviceLifecycleStatusByChannelResponseMessageProcessor(
            WebServiceResponseMessageSender responseMessageSender,
            @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap") MessageProcessorMap messageProcessorMap) {
        super(responseMessageSender, messageProcessorMap, MessageType.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL,
                ComponentType.DOMAIN_SMART_METERING);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        return responseMessage.getDataObject() instanceof SetDeviceLifecycleStatusByChannelResponseDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {
        this.managementService.handleSetDeviceLifecycleStatusByChannelResponse(deviceMessageMetadata,
                responseMessage.getResult(), responseMessage.getOsgpException(),
                (SetDeviceLifecycleStatusByChannelResponseDto) responseMessage.getDataObject());
    }

}
