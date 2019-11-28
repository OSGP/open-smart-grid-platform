/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.signing.server.infra.messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.signing.server.application.services.SigningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
            final int messagePriority = objectMessage.getJMSPriority();
            final RequestMessage requestMessage = (RequestMessage) objectMessage.getObject();
            final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = (UnsignedOslpEnvelopeDto) requestMessage
                    .getRequest();
            final String correlationUid = objectMessage.getJMSCorrelationID();
            final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            LOGGER.info("Received message of type: {}, for device: {} with correlationId: {} and replyToQueue: {}",
                    objectMessage.getJMSType(), deviceIdentification, correlationUid, replyToQueue);

            LOGGER.debug("-----------------------------------------------------------------------------");
            LOGGER.debug("messagePriority: {}", messagePriority);
            LOGGER.debug("unsignedOslpEnvelopeDto.getCorrelationUid() : {}",
                    unsignedOslpEnvelopeDto.getCorrelationUid());
            LOGGER.debug("unsignedOslpEnvelopeDto.getDeviceId() : {}", unsignedOslpEnvelopeDto.getDeviceId());
            LOGGER.debug("unsignedOslpEnvelopeDto.getDomain() : {}", unsignedOslpEnvelopeDto.getDomain());
            LOGGER.debug("unsignedOslpEnvelopeDto.getDomainVersion() : {}", unsignedOslpEnvelopeDto.getDomainVersion());
            LOGGER.debug("unsignedOslpEnvelopeDto.getIpAddress() : {}", unsignedOslpEnvelopeDto.getIpAddress());
            LOGGER.debug("unsignedOslpEnvelopeDto.getMessageType() : {}", unsignedOslpEnvelopeDto.getMessageType());
            LOGGER.debug("unsignedOslpEnvelopeDto.getMessagePriority() : {}",
                    unsignedOslpEnvelopeDto.getMessagePriority());
            LOGGER.debug("unsignedOslpEnvelopeDto.getOrganisationIdentification() : {}",
                    unsignedOslpEnvelopeDto.getOrganisationIdentification());
            LOGGER.debug("unsignedOslpEnvelopeDto.getPayloadMessage() : {}",
                    unsignedOslpEnvelopeDto.getPayloadMessage());
            LOGGER.debug("unsignedOslpEnvelopeDto.getRetryCount() : {}", unsignedOslpEnvelopeDto.getRetryCount());
            LOGGER.debug("unsignedOslpEnvelopeDto.getSequenceNumber() : {}",
                    unsignedOslpEnvelopeDto.getSequenceNumber());
            LOGGER.debug("unsignedOslpEnvelopeDto.isScheduled() : {}", unsignedOslpEnvelopeDto.isScheduled());
            LOGGER.debug("-----------------------------------------------------------------------------");

            this.signingService.sign(unsignedOslpEnvelopeDto, correlationUid, deviceIdentification, replyToQueue);

        } catch (final JMSException ex) {
            LOGGER.error("Exception: {} ", ex.getMessage(), ex);
        }
    }
}
