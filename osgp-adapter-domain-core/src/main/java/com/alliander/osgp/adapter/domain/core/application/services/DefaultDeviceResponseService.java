/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainCoreDefaultDeviceResponseService")
public class DefaultDeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeviceResponseService.class);

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    public void handleDefaultDeviceResponse(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final int messagePriority,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleDefaultDeviceResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        OsgpException osgpException = exception;

        if (deviceResult == ResponseMessageResultType.NOT_OK && exception == null) {
            LOGGER.error("Incorrect response received, exception should not be null when result is not ok");
            osgpException = new TechnicalException(ComponentType.DOMAIN_CORE, "An unknown error occurred");
        }
        if (deviceResult == ResponseMessageResultType.OK && exception != null) {
            LOGGER.error("Incorrect response received, result should be set to not ok when exception is not null");
            result = ResponseMessageResultType.NOT_OK;
        }

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(osgpException)
                .withMessagePriority(messagePriority).build();
        this.webServiceResponseMessageSender.send(responseMessage);
    }
}
