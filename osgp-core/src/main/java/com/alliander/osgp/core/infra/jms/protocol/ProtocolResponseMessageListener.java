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
        final ObjectMessage objectMessage = (ObjectMessage) message;
        final OsgpException osgpException = responseMessage.getOsgpException() == null ? null : responseMessage
                .getOsgpException();
        final String correlationUid = objectMessage.getJMSCorrelationID();
        final String messageType = objectMessage.getJMSType();
        final String domain = objectMessage.getStringProperty(Constants.DOMAIN);
        final String domainVersion = objectMessage.getStringProperty(Constants.DOMAIN_VERSION);
        final String organisationIdentification = objectMessage
                .getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        final ResponseMessageResultType responseMessageResultType = ResponseMessageResultType.valueOf(objectMessage
                .getStringProperty(Constants.RESULT));
        final Serializable dataObject = responseMessage.getDataObject();
        final boolean scheduled = objectMessage.propertyExists(Constants.IS_SCHEDULED) ? objectMessage
                .getBooleanProperty(Constants.IS_SCHEDULED) : false;
                final int retryCount = objectMessage.getIntProperty(Constants.RETRY_COUNT);
                final int messagePriority = message.getJMSPriority();

                // @formatter:off
                return new ProtocolResponseMessage.Builder()
                .domain(domain)
                .domainVersion(domainVersion)
                .messageType(messageType)
                .correlationUid(correlationUid)
                .organisationIdentification(organisationIdentification)
                .deviceIdentification(deviceIdentification)
                .result(responseMessageResultType)
                .osgpException(osgpException)
                .dataObject(dataObject)
                .scheduled(scheduled)
                .retryCount(retryCount)
                .messagePriority(messagePriority)
                .build();
                // @formatter:on
    }
}
