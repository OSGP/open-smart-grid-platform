/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.signing.server.infra.messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.oslp.UnsignedOslpEnvelopeDto;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.signing.server.application.services.SigningService;

@Component(value = "signingServerRequestsMessageListener")
public class SigningServerRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigningServerRequestMessageListener.class);

    @Autowired
    @Qualifier("SigningServerSigningService")
    private SigningService signingService;

    @Override
    public void onMessage(final Message message) {
        try {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final Destination replyToQueue = objectMessage.getJMSReplyTo();
            final RequestMessage requestMessage = (RequestMessage) objectMessage.getObject();
            final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = (UnsignedOslpEnvelopeDto) requestMessage
                    .getRequest();
            final String correlationUid = objectMessage.getJMSCorrelationID();
            final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            LOGGER.info("Received message of type: {}, for device: {} with correlationId: {} and replyToQueue: {}",
                    objectMessage.getJMSType(), deviceIdentification, correlationUid, replyToQueue.toString());

            LOGGER.info("-----------------------------------------------------------------------------");
            LOGGER.info("unsignedOslpEnvelopeDto.getCorrelationUid() : {}", unsignedOslpEnvelopeDto.getCorrelationUid());
            LOGGER.info("unsignedOslpEnvelopeDto.getDeviceId() : {}", unsignedOslpEnvelopeDto.getDeviceId());
            LOGGER.info("unsignedOslpEnvelopeDto.getDomain() : {}", unsignedOslpEnvelopeDto.getDomain());
            LOGGER.info("unsignedOslpEnvelopeDto.getDomainVersion() : {}", unsignedOslpEnvelopeDto.getDomainVersion());
            LOGGER.info("unsignedOslpEnvelopeDto.getIpAddress() : {}", unsignedOslpEnvelopeDto.getIpAddress());
            LOGGER.info("unsignedOslpEnvelopeDto.getMessageType() : {}", unsignedOslpEnvelopeDto.getMessageType());
            LOGGER.info("unsignedOslpEnvelopeDto.getOrganisationIdentification() : {}",
                    unsignedOslpEnvelopeDto.getOrganisationIdentification());
            LOGGER.info("unsignedOslpEnvelopeDto.getPayloadMessage() : {}", unsignedOslpEnvelopeDto.getPayloadMessage()
                    .toString());
            LOGGER.info("unsignedOslpEnvelopeDto.getRetryCount() : {}", unsignedOslpEnvelopeDto.getRetryCount());
            LOGGER.info("unsignedOslpEnvelopeDto.getSequenceNumber() : {}", unsignedOslpEnvelopeDto.getSequenceNumber());
            LOGGER.info("unsignedOslpEnvelopeDto.isScheduled() : {}", unsignedOslpEnvelopeDto.isScheduled());
            LOGGER.info("-----------------------------------------------------------------------------");

            this.signingService.sign(unsignedOslpEnvelopeDto, correlationUid, deviceIdentification, replyToQueue);

        } catch (final JMSException ex) {
            LOGGER.error("Exception: {} ", ex.getMessage(), ex);
        }
    }
}
