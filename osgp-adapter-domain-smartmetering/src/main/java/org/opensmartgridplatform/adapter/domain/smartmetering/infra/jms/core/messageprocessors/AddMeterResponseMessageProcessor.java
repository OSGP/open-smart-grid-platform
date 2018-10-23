/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class for processing smart metering default response messages
 */
@Component
public class AddMeterResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddMeterResponseMessageProcessor.class);

    @Autowired
    private InstallationService installationService;

    @Autowired
    protected AddMeterResponseMessageProcessor(
            WebServiceResponseMessageSender responseMessageSender,
            @Qualifier("domainSmartMeteringOsgpCoreResponseMessageProcessorMap") MessageProcessorMap messageProcessorMap) {
        super(responseMessageSender, messageProcessorMap, MessageType.ADD_METER, ComponentType.DOMAIN_SMART_METERING);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        // Only the result is used, no need to check the dataObject.
        return true;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) {

        this.installationService.handleAddMeterResponse(deviceMessageMetadata, responseMessage.getResult(),
                osgpException);
    }

    @Override
    protected void handleError(final Exception e, final DeviceMessageMetadata deviceMessageMetadata) {
        try {
            this.installationService.removeMeter(deviceMessageMetadata);
        } catch (final Exception ex) {
            LOGGER.error("Error removing meter {} for organization {} from core database with correlation UID {}",
                    deviceMessageMetadata.getDeviceIdentification(),
                    deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getCorrelationUid(),
                    ex);
        }

        super.handleError(e, deviceMessageMetadata);
    }
}
