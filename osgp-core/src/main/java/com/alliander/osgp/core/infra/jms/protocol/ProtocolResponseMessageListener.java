/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.core.application.services.DeviceResponseMessageService;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

// This class should fetch incoming messages from a responses queue.
public class ProtocolResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolResponseMessageListener.class);

    private final DeviceResponseMessageService deviceResponseMessageService;

    public ProtocolResponseMessageListener(final DeviceResponseMessageService deviceResponseMessageService) {
        this.deviceResponseMessageService = deviceResponseMessageService;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received protocol response message with correlationUid [{}] and type [{}]",
                    message.getJMSCorrelationID(), message.getJMSType());

            final ProtocolResponseMessage protocolResponseMessage = this.createResponseMessage(message);

            LOGGER.debug("OrganisationIdentification: [{}]", protocolResponseMessage.getOrganisationIdentification());
            LOGGER.debug("DeviceIdentification      : [{}]", protocolResponseMessage.getDeviceIdentification());
            LOGGER.debug("Domain                    : [{}]", protocolResponseMessage.getDomain());
            LOGGER.debug("DomainVersion             : [{}]", protocolResponseMessage.getDomainVersion());
            LOGGER.debug("Result                    : [{}]", protocolResponseMessage.getResult());
            LOGGER.debug("Description               : [{}]", protocolResponseMessage.getOsgpException());
            LOGGER.debug("MessagePriority           : [{}]", protocolResponseMessage.getMessagePriority());

            this.deviceResponseMessageService.processMessage(protocolResponseMessage);

        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    private ProtocolResponseMessage createResponseMessage(final Message message) throws JMSException {

        final ResponseMessage responseMessage = (ResponseMessage) ((ObjectMessage) message).getObject();
        final OsgpException osgpException = responseMessage.getOsgpException() == null ? null : responseMessage
                .getOsgpException();
        final String domain = message.getStringProperty(Constants.DOMAIN);
        final String domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
        final ResponseMessageResultType responseMessageResultType = ResponseMessageResultType.valueOf(message
                .getStringProperty(Constants.RESULT));
        final Serializable dataObject = responseMessage.getDataObject();
        boolean scheduled = false;
        if (message.propertyExists(Constants.IS_SCHEDULED)) {
            scheduled = message.getBooleanProperty(Constants.IS_SCHEDULED);
        }
        final int retryCount = message.getIntProperty(Constants.RETRY_COUNT);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(message);

        // @formatter:off
        return new ProtocolResponseMessage.Builder()
        .deviceMessageMetadata(deviceMessageMetadata)
        .domain(domain)
        .domainVersion(domainVersion)
        .result(responseMessageResultType)
        .osgpException(osgpException)
        .dataObject(dataObject)
        .scheduled(scheduled)
        .retryCount(retryCount)
        .build();
        // @formatter:on
    }
}
