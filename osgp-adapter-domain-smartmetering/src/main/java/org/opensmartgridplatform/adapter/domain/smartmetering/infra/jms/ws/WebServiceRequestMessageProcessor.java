/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.AbstractRequestMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 *
 */
public abstract class WebServiceRequestMessageProcessor extends AbstractRequestMessageProcessor implements
MessageProcessor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceRequestMessageProcessor.class);

    /**
     * This is the message sender needed for the message processor
     * implementations to handle the forwarding of messages to OSGP-CORE.
     */
    @Qualifier("domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    @Autowired
    protected OsgpCoreRequestMessageSender coreRequestMessageSender;

    /**
     * This is the message sender needed for the message processor
     * implementation to handle an error.
     */
    @Autowired
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    /**
     * The hash map of message processor instances.
     */
    @Autowired
    protected WebServiceRequestMessageProcessorMap webServiceRequestMessageProcessorMap;

    /**
     * The message type that a message processor implementation can handle.
     */
    protected DeviceFunction deviceFunction;

    /**
     * Construct a message processor instance by passing in the message type.
     *
     * @param deviceFunction
     *            The message type a message processor can handle.
     */
    protected WebServiceRequestMessageProcessor(final DeviceFunction deviceFunction) {
        this.deviceFunction = deviceFunction;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.webServiceRequestMessageProcessorMap.addMessageProcessor(this.deviceFunction.ordinal(),
                this.deviceFunction.name(), this);
    }

    /**
     * Indicates if the message processor contains a dataobject that should be
     * handled. Normally requests do contains some data, so the default is TRUE.
     *
     * @return Does the message contain a dataobject to be processed.
     */
    public boolean messageContainsDataObject() {
        return true;
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        Object dataObject = null;

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(message);

        try {
            dataObject = message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("device metadata: {}", deviceMessageMetadata);
            return;
        }

        try {
            LOGGER.info("Calling application service function: {}", deviceMessageMetadata.getMessageType());
            if (this.messageContainsDataObject()) {
                this.handleMessage(deviceMessageMetadata, dataObject);
            } else {
                this.handleMessage(deviceMessageMetadata);
            }

        } catch (final Exception e) {
            this.handleError(e, deviceMessageMetadata, "An unknown error occurred");
        }
    }

}
