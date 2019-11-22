/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.inbound;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.core.application.services.DeviceResponseMessageService;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final ProtocolResponseMessage protocolResponseMessage = createResponseMessage(message);

            LOGGER.debug("OrganisationIdentification: [{}]", protocolResponseMessage.getOrganisationIdentification());
            LOGGER.debug("DeviceIdentification      : [{}]", protocolResponseMessage.getDeviceIdentification());
            LOGGER.debug("Domain                    : [{}]", protocolResponseMessage.getDomain());
            LOGGER.debug("DomainVersion             : [{}]", protocolResponseMessage.getDomainVersion());
            LOGGER.debug("Result                    : [{}]", protocolResponseMessage.getResult());
            LOGGER.debug("Description               : [{}]", protocolResponseMessage.getOsgpException());
            LOGGER.debug("MessagePriority           : [{}]", protocolResponseMessage.getMessagePriority());
            LOGGER.debug("BypassRetry               : [{}]", protocolResponseMessage.bypassRetry());

            this.deviceResponseMessageService.processMessage(protocolResponseMessage);

        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    private static ProtocolResponseMessage createResponseMessage(final Message message) throws JMSException {
        return (ProtocolResponseMessage) ((ObjectMessage) message).getObject();
    }
}
