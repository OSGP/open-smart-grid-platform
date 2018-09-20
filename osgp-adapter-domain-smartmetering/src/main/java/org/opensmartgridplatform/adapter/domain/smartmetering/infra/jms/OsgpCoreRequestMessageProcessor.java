/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 *
 */
public abstract class OsgpCoreRequestMessageProcessor extends AbstractRequestMessageProcessor implements
        MessageProcessor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreRequestMessageProcessor.class);

    /**
     * This is the message sender needed for the message processor
     * implementations to handle the forwarding of messages to WS-SmartMetering.
     * <p>
     * Requests from OSGP-Core (which should be notifications not based on an
     * earlier request) are handled as the existing notification responses (that
     * do belong with earlier requests) from here. In this way, the system
     * handling the notifications does not have to make a distinction in the way
     * of handling these notification types.
     */
    @Autowired
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    /**
     * The map of message processor instances.
     */
    @Qualifier("domainSmartMeteringOsgpCoreRequestMessageProcessorMap")
    @Autowired
    protected MessageProcessorMap osgpCoreRequestMessageProcessorMap;

    /**
     * The message type that a message processor implementation can handle.
     */
    protected MessageType messageType;

    /**
     * Construct a message processor instance by passing in the message type.
     *
     * @param messageType
     *            The message type a message processor can handle.
     */
    protected OsgpCoreRequestMessageProcessor(final MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.osgpCoreRequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        Object dataObject = null;

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(message);

        try {
            dataObject = message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug(deviceMessageMetadata.toString());
            return;
        }

        try {
            LOGGER.info("Calling application service function: {}", deviceMessageMetadata.getMessageType());
            this.handleMessage(deviceMessageMetadata, dataObject);

        } catch (final Exception e) {
            this.handleError(e, deviceMessageMetadata, "Unexpected exception while retrieving message");
        }
    }

}
