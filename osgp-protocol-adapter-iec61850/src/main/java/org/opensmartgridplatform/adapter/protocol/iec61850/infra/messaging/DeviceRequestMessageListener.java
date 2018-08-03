/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.listener.SessionAwareMessageListener;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.NotSupportedException;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class DeviceRequestMessageListener implements SessionAwareMessageListener<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageListener.class);

    @Autowired
    @Qualifier("iec61850DeviceRequestMessageProcessorMap")
    private MessageProcessorMap iec61850RequestMessageProcessorMap;

    @Autowired
    private DeviceResponseMessageSender deviceResponseMessageSender;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.jms.listener.SessionAwareMessageListener#onMessage
     * (javax.jms.Message, javax.jms.Session)
     */
    @Override
    public void onMessage(final Message message, final Session session) throws JMSException {
        final ObjectMessage objectMessage = (ObjectMessage) message;
        String messageType = null;
        int messagePriority;
        MessageProcessor processor;
        try {
            messageType = message.getJMSType();
            messagePriority = message.getJMSPriority();
            LOGGER.info("Received message of type: {} with message priority: {}", messageType, messagePriority);
            processor = this.iec61850RequestMessageProcessorMap.getMessageProcessor(objectMessage);
        } catch (final IllegalArgumentException | JMSException e) {
            LOGGER.error("Unexpected IllegalArgumentException | JMSExceptionduring during onMessage(Message)", e);
            this.createAndSendException(objectMessage, messageType);
            return;
        }
        processor.processMessage(objectMessage);
    }

    private void createAndSendException(final ObjectMessage objectMessage, final String messageType) {
        this.sendException(objectMessage, new NotSupportedException(ComponentType.PROTOCOL_IEC61850, messageType));
    }

    private void sendException(final ObjectMessage objectMessage, final Exception exception) {
        try {
            final String domain = objectMessage.getStringProperty(Constants.DOMAIN);
            final String domainVersion = objectMessage.getStringProperty(Constants.DOMAIN_VERSION);
            final ResponseMessageResultType result = ResponseMessageResultType.NOT_OK;
            final FunctionalException osgpException = new FunctionalException(
                    FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION, ComponentType.PROTOCOL_IEC61850, exception);
            final Serializable dataObject = objectMessage.getObject();

            final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(objectMessage);
            final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage.Builder()
                    .deviceMessageMetadata(deviceMessageMetadata).domain(domain).domainVersion(domainVersion)
                    .result(result).osgpException(osgpException).dataObject(dataObject).scheduled(false).build();

            this.deviceResponseMessageSender.send(protocolResponseMessage);
        } catch (final Exception e) {
            LOGGER.error("Unexpected error during sendException(ObjectMessage, Exception)", e);
        }
    }
}
