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
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult,
            final OsgpException exception) {

        LOGGER.info("handleDefaultDeviceResponse for MessageType: {}", messageType);

        OsgpException osgpException = exception;
        ResponseMessageResultType result = ResponseMessageResultType.OK;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }
        } catch (final OsgpException e) {
            LOGGER.error("Unexpected OsgpException", e);
            osgpException = new TechnicalException(e.getComponentType(), e.getMessage(), e);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, null));
    }
}
