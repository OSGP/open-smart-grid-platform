/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.tariffswitching.application.services;

import org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "domainTariffSwitchingDefaultDeviceResponseService")
public class DefaultDeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeviceResponseService.class);

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    public void handleDefaultDeviceResponse(final CorrelationIds ids, final String messageType,
            final int messagePriority, final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleDefaultDeviceResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        OsgpException osgpException = exception;

        if (deviceResult == ResponseMessageResultType.NOT_OK && exception == null) {
            LOGGER.error("Incorrect response received, exception should not be null when result is not ok");
            osgpException = new TechnicalException(ComponentType.DOMAIN_TARIFF_SWITCHING, "An unknown error occurred");
        }
        if (deviceResult == ResponseMessageResultType.OK && exception != null) {
            LOGGER.error("Incorrect response received, result should be set to not ok when exception is not null");
            result = ResponseMessageResultType.NOT_OK;
        }

        final DeviceMessageMetadata metaData = DeviceMessageMetadata.newBuilder()
                .withCorrelationUid(ids.getCorrelationUid()).withDeviceIdentification(ids.getDeviceIdentification())
                .withOrganisationIdentification(ids.getOrganisationIdentification()).withMessageType(messageType)
                .build();

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withDeviceMessageMetadata(metaData).withResult(result).withOsgpException(osgpException)
                .withMessagePriority(messagePriority).build();

        this.webServiceResponseMessageSender.send(responseMessage);
    }
}
